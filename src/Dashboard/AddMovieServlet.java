package Dashboard;

import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import supers.BaseServlet;

import java.io.IOException;
import java.sql.*;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/_dashboard/api/add-movie")
public class AddMovieServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        JsonObject jsonResponse = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            String movieTitle = request.getParameter("movieTitle");
            String movieYearStr = request.getParameter("movieYear");
            String movieDirector = request.getParameter("movieDirector");
            String starName = request.getParameter("starName");
            String birthYearStr = request.getParameter("birthYear");
            String genreName = request.getParameter("genreName");
            int movieYear = Integer.parseInt(movieYearStr);
            Integer birthYear = (birthYearStr == null || birthYearStr.isEmpty()) ? null : Integer.parseInt(birthYearStr);

            CallableStatement stmt = conn.prepareCall("{CALL add_movie(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, movieTitle);
            stmt.setInt(2, movieYear);
            stmt.setString(3, movieDirector);
            stmt.setString(4, starName);
            if (birthYear != null) {
                stmt.setInt(5, birthYear);
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.setString(6, genreName);

            stmt.registerOutParameter(7, Types.VARCHAR);
            stmt.registerOutParameter(8, Types.VARCHAR);
            stmt.registerOutParameter(9, Types.INTEGER);

            boolean hasResult = stmt.execute();

            String newMovieId = stmt.getString(7);
            String newStarId = stmt.getString(8);
            int newGenreId = stmt.getInt(9);

            String statusMessage = "";
            if (hasResult) {
                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs.next()) {
                        statusMessage = rs.getString("message");
                    }
                }
            }

            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("movieId", newMovieId);
            jsonResponse.addProperty("starId", newStarId);
            jsonResponse.addProperty("genreId", newGenreId);
            jsonResponse.addProperty("message", statusMessage);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", e.getMessage());
        }

        response.getWriter().write(jsonResponse.toString());
    }
}
