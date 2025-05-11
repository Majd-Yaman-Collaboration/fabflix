import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import supers.BaseServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "MainPageServlet", urlPatterns = "/api/main-page")
public class MainPageServlet extends BaseServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        JsonObject jsonResponse = new JsonObject();
        JsonArray genresArray = new JsonArray();

        try (Connection conn = dataSource.getConnection();
             PrintWriter out = response.getWriter();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM genres")) {

            while (rs.next()) {
                genresArray.add(rs.getString("name"));
            }

            jsonResponse.add("genres", genresArray);
            out.write(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("error", e.getMessage());
            response.getWriter().write(jsonResponse.toString());
            response.setStatus(500);
        }
    }
}
