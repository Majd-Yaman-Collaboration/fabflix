import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashSet;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final HashSet<String> allowedURIs = new HashSet<>(); //better than an array

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();
//        System.out.println("LoginFilter: " + uri);

        // Check if this URL is allowed to access without logging in
        if (this.isUrlAllowedWithoutLogin(uri)) {
            // Keep default action: pass along the filter chain
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false); // don't create new session
        boolean loggedIn = (session != null) && (session.getAttribute("id") != null);

        if (loggedIn)
        {
            chain.doFilter(request, response); // allow access
        }
        else
        {
            httpResponse.sendRedirect("login.html"); // redirect to login
        }
    }
    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        return allowedURIs.stream().anyMatch(requestURI::endsWith);

    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("/");
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("login.css");
        allowedURIs.add("api/login");
        allowedURIs.add("FabflixLogo.png");
        allowedURIs.add("NavyFabflixLogo.png");
    }

    public void destroy() {
        // ignored.
    }

}