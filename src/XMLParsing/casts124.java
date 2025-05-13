package XMLParsing;

import org.xml.sax.SAXException;
import java.sql.*;
import java.util.*;

public class casts124 extends BaseXMLParsing
{
    HashMap<String,Actor> actorMap = new HashMap<>();
    private String current_director;
    private String current_title;

    final int batch_size = 500;

    int invalid_actors = 0;


    public static void main(String[] args)
    {
        String filename  = "casts124.xml";
        if (args.length > 0) filename = args[0];

        long start = System.currentTimeMillis();
        casts124 obj = new casts124();
        obj.parseDocument(filename);



        obj.insert_actors();
        long end = System.currentTimeMillis();
        System.out.println("Time Taken for casts124: " + (end - start));
        System.out.println("Total invalid actors: " + obj.invalid_actors);
    }



    public void insert_actors()
    {
        String insert_stars_query = "INSERT INTO stars VALUES (?,?,NULL)";
        String insert_stars_in_movies_query = "INSERT IGNORE INTO stars_in_movies VALUES (?,?)";

        String get_latest_id_query = "SELECT MAX(id) FROM stars";
        String get_movie_id_query = "SELECT id FROM movies WHERE title = ? AND director = ?";


        String latest_id = "nm0000000";

        try (Connection conn = get_connection())
        {
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            stmt.execute("SET FOREIGN_KEY_CHECKS=0;"); //optimization

            PreparedStatement insert_stars_ps = conn.prepareStatement(insert_stars_query);
            PreparedStatement get_movie_id_ps = conn.prepareStatement(get_movie_id_query);
            PreparedStatement insert_stars_in_movies_ps = conn.prepareStatement(insert_stars_in_movies_query);

            ResultSet latest_id_rs = stmt.executeQuery(get_latest_id_query);
            if (latest_id_rs.next()) latest_id = latest_id_rs.getString(1);


            int current_id = Integer.parseInt(latest_id.substring(2));

            int count = 0;
            for (Actor actor : actorMap.values())
            {
                ArrayList<String> movie_ids = new ArrayList<>();
                latest_id = "nm" + String.format("%07d",++current_id);


                insert_stars_ps.setString(1, latest_id);
                insert_stars_ps.setString(2, actor.name);
                insert_stars_ps.addBatch();

                for (Map.Entry<String,String> entry : actor.titles_directors.entrySet())
                {
                    get_movie_id_ps.setString(1, entry.getKey());
                    get_movie_id_ps.setString(2, entry.getValue());
                    ResultSet get_movie_id_rs =  get_movie_id_ps.executeQuery();
                    if (get_movie_id_rs.next()) movie_ids.add(get_movie_id_rs.getString(1));
                }
                for (String movie_id : movie_ids)
                {
                    insert_stars_in_movies_ps.setString(1, latest_id);
                    insert_stars_in_movies_ps.setString(2, movie_id);
                    insert_stars_in_movies_ps.addBatch();
                }

                if (++count % batch_size == 0)
                {
                    insert_stars_ps.executeBatch();
                    insert_stars_ps.clearBatch();

                    insert_stars_in_movies_ps.executeBatch();
                    insert_stars_in_movies_ps.clearBatch();
                }

            }


            insert_stars_ps.executeBatch();
            insert_stars_in_movies_ps.executeBatch();

            conn.commit();
            stmt.execute("SET FOREIGN_KEY_CHECKS=1;"); //optimization
            conn.setAutoCommit(true);
        }
        catch (SQLException e)
            {e.printStackTrace();}
    }



    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {

        Actor current_actor = new Actor();
        switch (qName.toLowerCase())
        {
            case "is": //is -> director name
                current_director = element_content.trim();
                break;
            case "t": //t -> movie title
                current_title = element_content.trim();
                break;
            case "a": //a -> actor name
                element_content = element_content.trim();
                //create new actor if name not already in Set.
                if (element_content.equals("none") || element_content.equals("no\\_actor"))
                {
                    ++invalid_actors;
                    break;
                }
                if (actorMap.containsKey(element_content))
                {
                    current_actor = actorMap.get(element_content.trim());
                }
                else
                { //grab the already existing actor
                    current_actor = new Actor();
                }

                current_actor.titles_directors.put(current_title,current_director);
                current_actor.name = element_content.trim();

                actorMap.put(current_actor.name, current_actor);
                break;
        }


    } //function end





}
