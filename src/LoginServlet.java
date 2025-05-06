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


    private void handle_error(String type, HttpServletResponse response)
    {
        try (PrintWriter out = response.getWriter())
        {
            JsonObject errorObject = new JsonObject();
            if (type.compareTo("email") == 0)
                errorObject.addProperty("error", "Invalid email");
            else if (type.compareTo("password") == 0)
                errorObject.addProperty("error", "Invalid password");

            out.write(errorObject.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            System.out.println("Recaptcha verification failed: " + e.getMessage());
            return;
        }

        response.setContentType("application/json");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        //Check email
        if (!email.contains("@") || !email.contains("."))
        {
            handle_error("email",response);
            return;
        }
        //Check password
        else if (password.isEmpty())
        {
            handle_error("password",response);
            return;
        }

        //check correctness of email and password sent
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
                out.write(successObject.toString());
                request.getSession(true).setAttribute("id", rs.getInt("id"));
            }
            else //user not found -> login info was wrong
            {
                JsonObject errorObject = new JsonObject();
                errorObject.addProperty("error", "Email or password is incorrect");
                out.write(errorObject.toString());

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            response.setStatus(500);
        }
    }



}
