import Interfaces.MovieListQueries;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import common.BaseServlet;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movies")
public class MovieListServlet extends BaseServlet implements MovieListQueries {
    private static final long serialVersionUID = 1L;



    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        HttpSession session = request.getSession();

        if (session.getAttribute("cart") == null)
        {
            ArrayList<String> cart = new ArrayList<>();
        }


        //filters (genre's the regexp from main page)
        String filterType =  request.getParameter("filter");
        String filterValue = request.getParameter("value");

        // if page exists, get number else default to 1
        int page = Integer.parseInt(request.getParameter("page") != null ? request.getParameter("page") : "1");
        int limit = Integer.parseInt(request.getParameter("limit") != null ? request.getParameter("limit") : "10");

        //search values
        String title = request.getParameter("title").toUpperCase();
        String year = request.getParameter("year").toUpperCase();
        String director = request.getParameter("director").toUpperCase();
        String star = request.getParameter("star").toUpperCase();

        String sortReq = request.getParameter("sort");
        String sortQueryAddOn = chooseSort(sortReq);






        int offset = (page - 1) * limit;

        try (PrintWriter out = response.getWriter(); Connection conn = dataSource.getConnection())
        {
            String query;
            PreparedStatement ps;
            String countQuery;
            PreparedStatement countPs;

            if ("genre".equals(filterType))
            {
                int q = 1;
                query = appendOrderByAndLimitOffsetToQuery(genreQuery,sortQueryAddOn);

                ps = conn.prepareStatement(query);
                ps.setString(q++, filterValue);
                ps.setInt(q++, limit);
                ps.setInt(q, offset);


                countQuery = genreCountQuery;
                countPs = conn.prepareStatement(countQuery);
                countPs.setString(1, filterValue);
            }
            else if ("title".equals(filterType))
            {
                if ("*".equals(filterValue))
                {
                    query = appendOrderByAndLimitOffsetToQuery(titleAsteriskQuery,sortQueryAddOn);

                    ps = conn.prepareStatement(query);
                    ps.setInt(1, limit);
                    ps.setInt(2, offset);

                    countQuery = titleAsteriskCountQuery;
                    countPs = conn.prepareStatement(countQuery);
                }
                else
                {
                    query = appendOrderByAndLimitOffsetToQuery(titleRegexpQuery,sortQueryAddOn);
                    ps = conn.prepareStatement(query);
                    ps.setString(1, filterValue.toUpperCase() + "%");
                    ps.setInt(2, limit);
                    ps.setInt(3, offset);

                    countQuery = titleRegexpCountQuery;
                    countPs = conn.prepareStatement(countQuery);
                    countPs.setString(1, filterValue.toUpperCase() + "%");
                }
            }
            //good application of demorgans low here
            else if (!(title.isEmpty() && year.isEmpty() && director.isEmpty() && star.isEmpty()))
            {

                query = appendOrderByAndLimitOffsetToQuery(searchQuery,sortQueryAddOn);;
                ps = conn.prepareStatement(query);

                //defaults
                if (title.isEmpty()) title = "%";
                else title = Arrays.stream(title.trim().split("\\s+"))
                        .map(token -> "+" + token + "*")
                        .collect(Collectors.joining(" "));
                if (year.isEmpty()) year = "-1";
                if (director.isEmpty()) director = "%";
                else director = "%" + director + "%";
                if (star.isEmpty()) star = "%";
                else star = "%" + star + "%";



                int q = 1;
                ps.setString(q++, title);
                //NOT A REDUNDANT LINE. there are 2 question marks in the query of this where these two can be used
                ps.setInt(q++, Integer.parseInt(year));
                ps.setInt(q++, Integer.parseInt(year));
                ps.setString(q++, director);
                ps.setString(q++, star);
                ps.setInt(q++, limit);
                ps.setInt(q, offset);


                q = 1;
                countQuery = searchCountQuery;
                countPs = conn.prepareStatement(countQuery);
                countPs.setString(q++, title.toUpperCase());
                //see comment above
                countPs.setInt(q++, Integer.parseInt(year));
                countPs.setInt(q++, Integer.parseInt(year));
                countPs.setString(q++, director);
                countPs.setString(q, star);


            }
            else
            {
                //standard
                query = appendOrderByAndLimitOffsetToQuery(standardQuery,sortQueryAddOn);;
                ps = conn.prepareStatement(query);
                ps.setInt(1, limit);
                ps.setInt(2, offset);

                countQuery = standardCountQuery;
                countPs = conn.prepareStatement(countQuery);
            }




            ResultSet countRs = countPs.executeQuery();
            int total = 0;
            if (countRs.next()) total = countRs.getInt("total");

            countRs.close();
            countPs.close();

            ResultSet rs = ps.executeQuery();

            JsonArray jsonArray = generateTableJson(rs,conn,new JsonArray());

            rs.close();
            ps.close();

            JsonObject responseObj = new JsonObject();
            responseObj.add("movies", jsonArray);
            responseObj.addProperty("total", total);
            responseObj.addProperty("page", page);
            responseObj.addProperty("limit", limit);
            responseObj.addProperty("totalPages", (int) Math.ceil((double) total / limit));

            out.write(responseObj.toString());
            response.setStatus(200);

        }
        catch (Exception e) {
            try {
                JsonObject error = new JsonObject();
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                error.addProperty("errorMessage", sw.toString());
                response.getWriter().write(error.toString());
                response.setStatus(500);
            } catch (Exception ignore) {}
        }


    }

    private String appendOrderByAndLimitOffsetToQuery(String query, String sort)
    {
        query += "ORDER BY " + sort + "LIMIT ? OFFSET ?";
        return query;
    }

    private String chooseSort(String sort)
    {
        final String DES = "DESC ";
        final String ASC = "ASC ";
        final String T = "m.title ";
        final String R = "r.rating ";
        String ret = "";
        if      (sort.equals("1")) ret = T + ASC + "," + R + DES;
        else if (sort.equals("2")) ret = T + ASC + "," + R + ASC;
        else if (sort.equals("3")) ret = T + DES + "," + R + DES;
        else if (sort.equals("4")) ret = T + DES + "," + R + ASC;
        else if (sort.equals("5")) ret = R + DES + "," + T + ASC;
        else if (sort.equals("6")) ret = R + DES + "," + T + DES;
        else if (sort.equals("7")) ret = R + ASC + "," + T + ASC;
        else if (sort.equals("8")) ret = R + ASC + "," + T + DES;
        return ret;
    }

    private JsonArray generateTableJson(ResultSet rs, Connection conn, JsonArray jsonArray) throws SQLException {
        while (rs.next())
        {

            String movieId = rs.getString("id");

            PreparedStatement genreStmt = conn.prepareStatement(
                    "SELECT g.name FROM genres_in_movies gm JOIN genres g ON gm.genreId = g.id WHERE gm.movieId = ? LIMIT 3");
            genreStmt.setString(1, movieId);
            ResultSet genreRs = genreStmt.executeQuery();
            JsonArray genreArray = new JsonArray();
            while (genreRs.next()) {
                genreArray.add(genreRs.getString("name"));
            }

            PreparedStatement starStmt = conn.prepareStatement(
                    "SELECT s.id, s.name FROM stars_in_movies sm JOIN stars s ON sm.starId = s.id WHERE sm.movieId = ? LIMIT 3");
            starStmt.setString(1, movieId);
            ResultSet starRs = starStmt.executeQuery();
            JsonArray starArray = new JsonArray();


            while (starRs.next())
            {
                JsonObject starObj = new JsonObject();
                starObj.addProperty("id", starRs.getString("id"));
                starObj.addProperty("name", starRs.getString("name"));
                starArray.add(starObj);
            }

            JsonObject movieObject = new JsonObject();
            movieObject.addProperty("id", movieId);
            movieObject.addProperty("title", rs.getString("title"));
            movieObject.addProperty("year", rs.getInt("year"));
            movieObject.addProperty("director", rs.getString("director"));
            movieObject.addProperty("rating", rs.getFloat("rating"));
            movieObject.add("genres", genreArray);
            movieObject.add("stars", starArray);

            jsonArray.add(movieObject);
            genreRs.close();
            genreStmt.close();
            starRs.close();
            starStmt.close();
        }
        return jsonArray;
    } //function end


} //class end

