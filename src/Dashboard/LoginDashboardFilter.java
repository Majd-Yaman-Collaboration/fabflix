package Dashboard;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginDashboardFilter", urlPatterns = "/_dashboard/*")
public class LoginDashboardFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>(); //better than an array

    public void init(FilterConfig fConfig)
    {
        allowedURIs.add("dashboard-login.html");
        allowedURIs.add("dashboard-login.js");
        allowedURIs.add("login.css");
        allowedURIs.add("_dashboard/api/login-dashboard");

        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");

        allowedURIs.add("FabflixLogo.png");
        allowedURIs.add("NavyFabflixLogo.png");
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        return allowedURIs.stream().anyMatch(requestURI::endsWith);
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String      uri = httpRequest.getRequestURI();
        // Check if this URL is allowed to access without logging in
        boolean     allowed = isUrlAllowedWithoutLogin(uri);
        HttpSession session = httpRequest.getSession(false); // don't create new session
        boolean     loggedIn = (session != null) && (session.getAttribute("employee") != null);

        //TODO COMMENT AFTER FINISHING WITH EVERYTHING ELSE
//        loggedIn = true;
//        allowed = true;

        if (loggedIn || allowed)
            chain.doFilter(request, response); // allow access
        else
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/_dashboard/dashboard-login.html"); // redirect to login

    }

    public void destroy() {
        // ignored.
    }

}