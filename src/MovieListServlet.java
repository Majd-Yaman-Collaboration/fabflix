import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movies")
public class MovieListServlet extends BaseServlet implements MovieListQueries {
    private static final long serialVersionUID = 1L;



    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");

        //filters (genre's the regexp from main page)
        String filterType = request.getParameter("filter");
        String filterValue = request.getParameter("value");
        // if page exists, get number else default to 1
        int page = Integer.parseInt(request.getParameter("page") != null ? request.getParameter("page") : "1");
        int limit = Integer.parseInt(request.getParameter("limit") != null ? request.getParameter("limit") : "10");

        //search values
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");




        int offset = (page - 1) * limit;

        try (PrintWriter out = response.getWriter(); Connection conn = dataSource.getConnection())
        {
            String query;
            PreparedStatement ps;
            String countQuery;
            PreparedStatement countPs;

            if ("genre".equals(filterType)) {
                query = genreQuery;
                ps = conn.prepareStatement(query);
                ps.setString(1, filterValue);
                ps.setInt(2, limit);
                ps.setInt(3, offset);

                countQuery = genreCountQuery;
                countPs = conn.prepareStatement(countQuery);
                countPs.setString(1, filterValue);
            } else if ("title".equals(filterType)) {
                if ("*".equals(filterValue)) {
                    query = titleAsteriskQuery;
                    ps = conn.prepareStatement(query);
                    ps.setInt(1, limit);
                    ps.setInt(2, offset);

                    countQuery = titleAsteriskCountQuery;
                    countPs = conn.prepareStatement(countQuery);
                }
                else
                {
                    query = titleRegexpQuery;
                    ps = conn.prepareStatement(query);
                    ps.setString(1, filterValue.toUpperCase() + "%");
                    ps.setInt(2, limit);
                    ps.setInt(3, offset);

                    countQuery = titleRegexpCountQuery;
                    countPs = conn.prepareStatement(countQuery);
                    countPs.setString(1, filterValue.toUpperCase() + "%");
                }
            }
            else
            {
                //standard
                query = standardQuery;
                ps = conn.prepareStatement(query);
                ps.setInt(1, limit);
                ps.setInt(2, offset);

                countQuery = standardCountQuery;
                countPs = conn.prepareStatement(countQuery);
            }

            ResultSet countRs = countPs.executeQuery();
            int total = 0;
            if (countRs.next()) {
                total = countRs.getInt("total");
            }
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
                    "SELECT s.id, s.name " +
                            "FROM stars_in_movies sm " +
                            "JOIN stars s ON sm.starId = s.id " +
                            "LEFT JOIN (SELECT starId, COUNT(*) AS movie_count FROM stars_in_movies GROUP BY starId) " +
                            "star_count ON star_count.starId = s.id " +
                            "WHERE sm.movieId = ? " +
                            "ORDER BY star_count.movie_count DESC, s.name ASC LIMIT 3");
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

