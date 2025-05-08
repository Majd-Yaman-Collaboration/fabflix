package Dashboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import supers.BaseServlet;

import java.sql.DatabaseMetaData;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;

@WebServlet(name = "MetadataServlet", urlPatterns = "/api/_dashboard/metadata")
public class MetadataServlet extends BaseServlet
{
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter(); Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "%", null);

            JsonArray tablesArray = new JsonArray();
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tablesArray.add(tableName);
            }

            JsonObject tablesObject = new JsonObject();
            tablesObject.add("tables", tablesArray);
            out.write(tablesObject.toString());
        } catch (Exception e) {
            try {
                JsonObject error = new JsonObject();
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                error.addProperty("errorMessage", sw.toString());
                response.getWriter().write(error.toString());
                response.setStatus(500);
            } catch (Exception ignore) {}
        }
    }
}
