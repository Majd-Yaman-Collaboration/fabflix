package common.Dashboard;

import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import supers.BaseServlet;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

@WebServlet(name = "AddStarServlet", urlPatterns = "/_dashboard/api/add-star")
public class AddStarServlet extends BaseServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        JsonObject jsonResponse = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            String name = request.getParameter("name");
            String birthYearStr = request.getParameter("birthYear");
            Integer birthYear = (birthYearStr == null || birthYearStr.isEmpty()) ? null : Integer.parseInt(birthYearStr);

            CallableStatement stmt = conn.prepareCall("{CALL add_star(?, ?, ?)}");
            stmt.setString(1, name);
            if (birthYear != null) {
                stmt.setInt(2, birthYear);
            }
            else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.execute();

            String newId = stmt.getString(3);
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("starId", newId);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("error", e.getMessage());
        }

        response.getWriter().write(jsonResponse.toString());
    }
}
