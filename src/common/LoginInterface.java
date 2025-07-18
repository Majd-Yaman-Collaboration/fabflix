package common;

import common.ReCAPTCHA.RecaptchaVerifyUtils;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

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
            if (rs.next())
            {
                String encryptedPassword = rs.getString("password");

                if (new StrongPasswordEncryptor().checkPassword(password, encryptedPassword))
                {
                    JsonObject successObject = new JsonObject();
                    successObject.addProperty("status", "success");

                    out.write(successObject.toString());

                    //SESSION / COOKIES / JWT
                    Map<String, Object> claims = new HashMap<>();
                    //used in loginfilter and confirmation page (needs user id)



                    if (userType.equals("employee"))
                        claims.put("employee", true);
                    else
                        claims.put("customer", rs.getInt("id"));



                    String token = JwtUtil.generateToken("user", claims);
                    JwtUtil.updateJwtCookie(request, response, token);
                    for (int i = 0; i < 20; ++i) //printed multiple times just to 100% see it
                        System.out.println("JWT LOGIN SUCCESS, CLAIMS UPDATED TO: " + claims);
                }
                else
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
