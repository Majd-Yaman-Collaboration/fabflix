import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends BaseServlet implements RecaptchaLoginInterface{
    private static final long serialVersionUID = 1L;

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        response = implementRecaptcha(gRecaptchaResponse, response);

        response.setContentType("application/json");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (!email.contains("@") || !email.contains(".")) {
            handle_error("email", response);
            return;
        } else if (password.isEmpty()) {
            handle_error("password", response);
            return;
        }

        try (PrintWriter out = response.getWriter(); Connection conn = dataSource.getConnection()) {
            String query = "SELECT id, password FROM customers WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String encryptedPassword = rs.getString("password");
                int userId = rs.getInt("id");

                if (new StrongPasswordEncryptor().checkPassword(password, encryptedPassword)) {
                    JsonObject successObject = new JsonObject();
                    successObject.addProperty("status", "success");
                    out.write(successObject.toString());
                    request.getSession(true).setAttribute("id", userId);
                } else {
                    handle_error("password", response);
                }
            } else {
                handle_error("email", response);
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }
}
