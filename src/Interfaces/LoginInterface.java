package Interfaces;

import ReCAPTCHA.RecaptchaVerifyUtils;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface LoginInterface
{



    default void implementRecaptcha(String gRecaptchaResponse, HttpServletResponse response)
    {
        try
        {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e)
        {
            response.setContentType("application/json");
            try (PrintWriter out = response.getWriter()) {
                JsonObject errorObject = new JsonObject();
                errorObject.addProperty("error", "Please complete the reCAPTCHA");
                out.write(errorObject.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    default void handle_login_verification_through_database(String email, String password, HttpServletRequest request,HttpServletResponse response, DataSource dataSource, String query, String userType)
    {
        try (PrintWriter out = response.getWriter(); Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String encryptedPassword = rs.getString("password");

                if (new StrongPasswordEncryptor().checkPassword(password, encryptedPassword))
                {

                    JsonObject successObject = new JsonObject();
                    successObject.addProperty("status", "success");
                    out.write(successObject.toString());
                    HttpSession session =  request.getSession(true);


                    if (userType.equals("employee"))
                        session.setAttribute(userType, true);
                    else
                        session.setAttribute("id",rs.getInt("id"));


                    session.setAttribute("customer", true); //need customer ability either way.
                } else
                {
                    handle_error("password", response);
                }
            }
            else
            {
                handle_error("email", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }

    default void handle_email_and_password_errors(String email, String password, HttpServletResponse response)
    {
        if (!email.contains("@") || !email.contains("."))
            handle_error("email", response);

         else if (password.isEmpty())
            handle_error("password", response);

    }

    default void handle_error(String type, HttpServletResponse response)
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
}
