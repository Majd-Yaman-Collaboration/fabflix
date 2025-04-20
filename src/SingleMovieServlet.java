import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends BaseServlet {
    private static final long serialVersionUID = 2L;

    //init() function inherited

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter(); Connection conn = dataSource.getConnection()) {
            String movieId = request.getParameter("id");

            String query = "SELECT m.title, m.year, m.director, r.rating " +
                    "FROM movies m JOIN ratings r ON m.id = r.movieId " +
                    "WHERE m.id = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, movieId);
            ResultSet rs = ps.executeQuery();

            JsonObject movieObject = new JsonObject();

            if (rs.next()) {
                movieObject.addProperty("title", rs.getString("title"));
                movieObject.addProperty("year", rs.getInt("year"));
                movieObject.addProperty("director", rs.getString("director"));
                movieObject.addProperty("rating", rs.getFloat("rating"));
            }

            rs.close();
            ps.close();

            // for the genres
            PreparedStatement genreStmt = conn.prepareStatement(
                    "SELECT g.name FROM genres_in_movies gm JOIN genres g ON gm.genreId = g.id WHERE gm.movieId = ?");
            genreStmt.setString(1, movieId);
            ResultSet genreRs = genreStmt.executeQuery();
            JsonArray genreArray = new JsonArray();
            while (genreRs.next()) {
                genreArray.add(genreRs.getString("name"));
            }
            movieObject.add("genres", genreArray);
            genreRs.close();
            genreStmt.close();

            // for the stars
            PreparedStatement starStmt = conn.prepareStatement(
                    "SELECT s.id, s.name FROM stars_in_movies sm JOIN stars s ON sm.starId = s.id WHERE sm.movieId = ?");
            starStmt.setString(1, movieId);
            ResultSet starRs = starStmt.executeQuery();
            JsonArray starArray = new JsonArray();
            while (starRs.next()) {
                JsonObject starObj = new JsonObject();
                starObj.addProperty("id", starRs.getString("id"));
                starObj.addProperty("name", starRs.getString("name"));
                starArray.add(starObj);
            }
            movieObject.add("stars", starArray);
            starRs.close();
            starStmt.close();

            out.write(movieObject.toString());
            response.setStatus(200);

        } catch (Exception e) {
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
}
