package common.Dashboard;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import supers.BaseServlet;

import java.io.IOException;

@WebServlet(name = "DashboardServet", urlPatterns = "/api/_dashboard")
public class DashboardServlet extends BaseServlet
{
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("_dashboard/dashboard-login.html");
    }
}
