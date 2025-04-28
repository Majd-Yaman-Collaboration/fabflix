import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

//RECIEVES: customerId, movieId, saleDate
//SENDS: sale id



@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation-servlet")
public class ConfirmationServlet extends BaseServlet
{
    private static final long serialVersionUID = 1L;
    private String insertQuery =
            "INSERT INTO sales (customerId, movieId, saleDate) VALUES (?, ?, ?)";
    private String idQuery = "SELECT id FROM sales WHERE movieId = ?";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        int customerId = Integer.parseInt(request.getSession().getAttribute("id").toString());
        String movieId = request.getParameter("movieId");
        String saleDate = request.getParameter("saleDate");

        try (Connection conn = dataSource.getConnection(); PrintWriter out = response.getWriter())
        {
            PreparedStatement ps = conn.prepareStatement(insertQuery);
            int q = 1;
            ps.setInt(q++, customerId);
            ps.setString(q++, movieId);
            ps.setString(q, saleDate);
            ps.executeUpdate();

            PreparedStatement psId = conn.prepareStatement(idQuery);
            psId.setString(1, movieId);
            ResultSet rsId = psId.executeQuery();

            if (!rsId.next()) return;

            String id = rsId.getString("id");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("customerId", customerId);
            jsonObject.addProperty("id", id);
            out.write(jsonObject.toString());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
