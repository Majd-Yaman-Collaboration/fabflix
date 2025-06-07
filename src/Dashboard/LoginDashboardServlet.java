package Dashboard;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import supers.BaseServlet;
import Common.LoginInterface;

@WebServlet(name = "LoginDashboardServlet", urlPatterns = "/_dashboard/api/login-dashboard")
public class LoginDashboardServlet extends BaseServlet implements LoginInterface {
    private static final long serialVersionUID = 1L;


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        implementRecaptcha(gRecaptchaResponse, response);

        response.setContentType("application/json");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        handle_email_and_password_errors(email, password, response);

        String query = "SELECT password FROM employees WHERE email = ?";
        handle_login_verification_through_database(email,password,request,response,dataSource,query,"employee");
    }
}
