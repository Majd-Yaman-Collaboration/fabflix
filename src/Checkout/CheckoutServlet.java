package Checkout;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import supers.BaseServlet;

@WebServlet(name = "CheckoutServlet", urlPatterns = "/api/checkout-servlet")
public class CheckoutServlet extends BaseServlet
{
    private static final long serialVersionUID = 1L;
    private String query =
            "SELECT * " +
            "FROM creditcards cc " +
            "WHERE cc.id = ? AND " +
            "cc.firstName = ? AND " +
            "cc.lastName = ? AND " +
            "cc.expiration = ?";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String first = request.getParameter("firstName");
        String last = request.getParameter("lastName");
        String card = request.getParameter("cardNumber");
        String expiry = request.getParameter("expirationDate");
        System.out.println("First: " + first);
        System.out.println("Last: " + last);
        System.out.println("Card: " + card);
        System.out.println("Expiry: " + expiry);

        try (Connection conn = dataSource.getConnection(); PrintWriter out = response.getWriter())
        {
            PreparedStatement ps = conn.prepareStatement(query);
            int q = 1;
            ps.setString(q++, card);
            ps.setString(q++, first);
            ps.setString(q++, last);
            ps.setString(q, expiry);

            ResultSet rs = ps.executeQuery();

            //true means found
            JsonObject jsonObject = new JsonObject();
            if (rs.next())
                jsonObject.addProperty("status", "success");
            else
                jsonObject.addProperty("status", "fail");
            out.write(jsonObject.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }





    }
}


