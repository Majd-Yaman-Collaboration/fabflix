package star;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import common.BaseServlet;

@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends BaseServlet {
    private static final long serialVersionUID = 2L;

    //init() function inherited

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter(); Connection conn = dataSource.getConnection()) {
            String id = request.getParameter("id");
            String query =
                    "SELECT s.id AS starId, s.name, s.birthYear, m.id AS movieId, m.title " +
                    "FROM stars s " +
                    "JOIN stars_in_movies sm ON s.id = sm.starId " +
                    "JOIN movies m ON sm.movieId = m.id " +
                    "WHERE s.id = ? " +
                    "ORDER BY m.year DESC, m.title ASC";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();

            JsonObject result = new JsonObject();
            JsonArray movies = new JsonArray();

            boolean firstRow = true;
            while (rs.next()) {
                if (firstRow) {
                    result.addProperty("id", rs.getString("starId"));
                    result.addProperty("name", rs.getString("name"));
                    result.addProperty("birthYear", rs.getString("birthYear"));
                    firstRow = false;
                }

                JsonObject movieObj = new JsonObject();
                movieObj.addProperty("id", rs.getString("movieId"));
                movieObj.addProperty("title", rs.getString("title"));
                movies.add(movieObj);
            }

            result.add("movies", movies);
            out.write(result.toString());
            response.setStatus(200);

            rs.close();
            statement.close();
        } catch (Exception e) {
            try {
                JsonObject error = new JsonObject();
                error.addProperty("errorMessage", e.getMessage());
                response.getWriter().write(error.toString());
                response.setStatus(500);
            } catch (Exception ignore) {}
        }
    }
}
