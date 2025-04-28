import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends BaseServlet
{
    private static final long serialVersionUID = 1L;
    private Map<String,Integer> movieIds;




    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();

        if (session.getAttribute("movieIds") == null)
        {
            movieIds = new TreeMap<String,Integer>();
            session.setAttribute("movieIds", movieIds);
        }
        else movieIds = (TreeMap<String,Integer>) session.getAttribute("movieIds");



        try (PrintWriter out = response.getWriter())
        {


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



    }
}
