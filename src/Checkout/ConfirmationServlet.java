package Checkout;

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
import java.sql.Statement;
import supers.BaseServlet;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

//RECIEVES: customerId, movieId, saleDate
//SENDS: sale id



@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation-servlet")
public class ConfirmationServlet extends HttpServlet
{
    DataSource dataSource;
    @Override
    public void init() {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb-slave");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private static final long serialVersionUID = 1L;
    private String insertQuery =
            "INSERT INTO sales (customerId, movieId, saleDate, quantity) VALUES (?, ?, ?, ?)";
    private String retQuery =
            "SELECT m.title, r.rating " +
            "FROM sales s " +
            "JOIN movies m ON s.movieId = m.id " +
            "JOIN ratings r ON m.id = r.movieId " +
            "WHERE s.id = ?;"
            ;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        int customerId = Integer.parseInt(request.getSession().getAttribute("id").toString());
        String movieId = request.getParameter("movieId");
        String saleDate = request.getParameter("saleDate");
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        try (Connection conn = dataSource.getConnection(); PrintWriter out = response.getWriter())
        {
            //Statement.RETURN_GENERATED_KEYS <- gives access to generated stuff like AUTO_INCREMENT
            PreparedStatement ps = conn.prepareStatement(insertQuery,Statement.RETURN_GENERATED_KEYS);
            int q = 1;
            ps.setInt(q++, customerId);
            ps.setString(q++, movieId);
            ps.setString(q++, saleDate);
            ps.setInt(q, quantity);
            ps.executeUpdate();

            //gets the AUTO_INTEGER generated id from the sales update.
            ResultSet generatedRs = ps.getGeneratedKeys();
            if (!generatedRs.next()) return;
            int saleId = generatedRs.getInt(1);


            //------
            PreparedStatement psRet = conn.prepareStatement(retQuery);
            psRet.setInt(1, saleId);
            ResultSet rsRet = psRet.executeQuery();

            if (!rsRet.next()) return;


            String title = rsRet.getString("title");
            double rating = rsRet.getDouble("rating");

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("saleId", saleId);
            jsonObject.addProperty("title", title);
            //he already has quantity/
            jsonObject.addProperty("rating", rating);

            out.write(jsonObject.toString());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
