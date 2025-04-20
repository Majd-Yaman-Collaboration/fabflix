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


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends BaseServlet{
    private static final long serialVersionUID = 1L;
    private final String query = "SELECT c.id FROM customer WHERE c.email = ? AND c.password = ?";



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection conn = dataSource.getConnection())
        {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) //found user
            {

            }
            else //user not found -> login info was wrong
            {

            }

        }
        catch (Exception e)
        {

        }
    }



}
