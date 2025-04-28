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
import com.google.gson.Gson;

@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends BaseServlet
{
    private static final long serialVersionUID = 1L;
    //ID,Quant
    private Map<String,Integer> movieIds;




    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");

        HttpSession session = request.getSession();

        if (session.getAttribute("movieIds") == null)
        {
            movieIds = new TreeMap<String,Integer>();
            session.setAttribute("movieIds", movieIds);
        }
        //grab the already defined movieIds from the session
        else movieIds = (TreeMap<String,Integer>) session.getAttribute("movieIds");


        String id = request.getParameter("movieId");
        if (id == null)
        {
            System.out.println(movieIds);

            try (PrintWriter out = response.getWriter())
            {
                Gson gson = new Gson();
                String json = gson.toJson(movieIds);
                out.write(json);
                return;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        System.out.println(id);

        //if movieIds not defined yet -> define it


        //add button after first
        if (movieIds.containsKey(id))
            movieIds.put(id, movieIds.get(id) + 1);
        //initial add
        else movieIds.put(id, 1);






    }
}
