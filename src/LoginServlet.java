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
    private final String query = "SELECT c.id FROM customers c WHERE c.email = ? AND c.password = ?";


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        response.setContentType("application/json");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        System.out.println("email: " + email);
        System.out.println("password: " + password);



        JsonArray jsonArray = new JsonArray();

        if (!email.contains("@") || !email.contains("."))
        {
            try (PrintWriter out = response.getWriter())
            {
                JsonObject errorObject = new JsonObject();
                errorObject.addProperty("error", "Invalid email address");
                out.write(errorObject.toString());
                return; //quick return to display error to user
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }


        try (PrintWriter out = response.getWriter(); Connection conn = dataSource.getConnection())
        {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs= ps.executeQuery();




            if (rs.next()) //found user
            {
                JsonObject successObject = new JsonObject();
                successObject.addProperty("status", "success");
                jsonArray.add(successObject);



            }
            else //user not found -> login info was wrong
            {
                JsonObject errorObject = new JsonObject();
                errorObject.addProperty("error", "Email or password is incorrect");
                jsonArray.add(errorObject);
            }

            out.write(jsonArray.toString());
            rs.close();
            conn.close();
            ps.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JsonObject error = new JsonObject();
            error.addProperty("errorMessage", e.getMessage());
            jsonArray.add(error);
            response.setStatus(500);
        }
    }



}
