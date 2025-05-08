import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

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
