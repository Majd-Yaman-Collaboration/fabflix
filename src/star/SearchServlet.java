package star;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import common.BaseServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String mode = request.getParameter("mode");
        String query = request.getParameter("title-input");

        if (query == null || query.trim().isEmpty()) {
            out.write("[]");
            return;
        }

        String[] tokens = query.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            sb.append("+").append(token).append("* ");
        }
        String fullTextQuery = sb.toString().trim();

        try (Connection conn = dataSource.getConnection()) {
            if ("autocomplete".equals(mode)) {
                JsonArray suggestions = new JsonArray();
                int dist = Math.max(1, query.length() / 4);
                String sql =
                        "SELECT id, title FROM movies " +
                        "WHERE MATCH(title) AGAINST(? IN BOOLEAN MODE) " +
                        //"OR edth(UPPER(title), ?, " + dist + ") = 1 " +
                        "LIMIT 10";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, fullTextQuery);
                    ps.setString(2, query.toUpperCase());
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        JsonObject suggestion = new JsonObject();
                        suggestion.addProperty("value", rs.getString("title"));

                        JsonObject data = new JsonObject();
                        data.addProperty("movieId", rs.getString("id"));

                        suggestion.add("data", data);
                        suggestions.add(suggestion);
                    }

                    out.write(suggestions.toString());
                }
            } else if ("fulltext".equals(mode)) {
                JsonArray results = new JsonArray();
                int dist = Math.max(1, query.length() / 4);
                String sql =
                        "SELECT id, title FROM movies " +
                                "WHERE MATCH(title) AGAINST(? IN BOOLEAN MODE) " +
                               // "OR edth(UPPER(title), ?, " + dist + ") = 1 " +
                                "LIMIT 10";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, fullTextQuery);
                    ps.setString(2, query.toUpperCase());
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        JsonObject movie = new JsonObject();
                        movie.addProperty("id", rs.getString("id"));
                        movie.addProperty("title", rs.getString("title"));
                        movie.addProperty("year", rs.getInt("year"));
                        movie.addProperty("director", rs.getString("director"));

                        results.add(movie);
                    }

                    out.write(results.toString());
                }
            } else {
                JsonObject error = new JsonObject();
                error.addProperty("error", "Invalid mode");
                out.write(error.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject error = new JsonObject();
            error.addProperty("errorMessage", e.getMessage());
            out.write(error.toString());
        }

        out.close();
    }
}
