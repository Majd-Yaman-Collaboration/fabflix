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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

        //load prev session^^^^


        String id = request.getParameter("movieId");
        String cartItems = request.getParameter("cartItems");

        if (id == null && cartItems == null)
        {

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
        else if (cartItems != null)
        {
            //sample input
            //[{"movieId":"tt0315354","quantity":3},{"movieId":"tt0349955","quantity":4},{"movieId":"tt0420975","quantity":4}]

            String cartItemsJson = request.getParameter("cartItems");
            Gson gson = new Gson();
            JsonArray cartItemsArray = gson.fromJson(cartItemsJson, JsonArray.class);

            movieIds = new TreeMap<String,Integer>();
            session.setAttribute("movieIds", movieIds); //new obj to replace the old

            for (int i = 0; i < cartItemsArray.size(); i++) {
                JsonObject item = cartItemsArray.get(i).getAsJsonObject();
                String movieId = item.get("movieId").getAsString();
                int quantity = item.get("quantity").getAsInt();
                movieIds.put(movieId, quantity);
            }
            return;

        }



       //else just add 1 to the id.
        add1OrInitialize1(id);



    }

    private void add1OrInitialize1(String id)
    {
        if (movieIds.containsKey(id))
            movieIds.put(id, movieIds.get(id) + 1);
            //initial add
        else movieIds.put(id, 1);
    }
}
