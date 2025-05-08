import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

public interface RecaptchaLoginInterface
{
    default HttpServletResponse implementRecaptcha(String gRecaptchaResponse, HttpServletResponse response)
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
        return response;
    }
}
