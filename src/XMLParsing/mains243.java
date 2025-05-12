package XMLParsing;

import ObjectClasses.Movie;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import supers.BaseServlet;

import java.sql.*;
import java.util.*;



public class mains243 extends BaseXMLParsing
{
    Set<Movie> movieList = new HashSet<Movie>();
    ArrayList<String> cats_in_movie = new ArrayList<String>();
    Set<String> unique_cats = new HashSet<String>();
//    String element_content; now in the SuperClass
    String current_director;
    private Movie current_movie;
    final int batch_size = 500;
    int current_id = 0;
    String latest_id;


    public static void main(String[] args)
    {
        run_mains243();
    }

    public static void run_mains243()
    {
        long start = System.currentTimeMillis();
        System.out.println("Creating main243 objects...");
        mains243 obj = new mains243();

        System.out.println("Parsing document");
        obj.parseDocument("mains243.xml");

        System.out.println("Inserting into the database");
        obj.insert_all_movies();
        long end = System.currentTimeMillis();
        System.out.println("Time taken: " + (end - start)); //
    }


    private void insert_all_genres(Connection conn) throws Exception
    {
        String insert_genres_query = "INSERT IGNORE INTO genres (name) VALUES (?);";
        PreparedStatement ps_genres = conn.prepareStatement(insert_genres_query);
        for (String genre : unique_cats)
        {
            ps_genres.setString(1,genre);
            ps_genres.addBatch();
        }
        ps_genres.executeBatch();
    }

    private void insert_all_movies()
    {
        String insert_movies_query = "INSERT INTO movies VALUES (?,?,?,?)";

        String get_latest_id_query = "SELECT MAX(id) FROM movies";

        String insert_genres_in_movies_query = "INSERT IGNORE INTO genres_in_movies (genreId, movieId) " +
                "SELECT id, ? FROM genres WHERE name = ?";

        //ID, RATING, NUMVOTES
        String insert_rating = "INSERT INTO ratings VALUES (?, 0.0, 0)";



        try (Connection conn = get_connection())
        {
            Statement stmt = conn.createStatement();
            stmt.execute("SET FOREIGN_KEY_CHECKS=0;"); //optimization
            conn.setAutoCommit(false);

            PreparedStatement ps_movies = conn.prepareStatement(insert_movies_query);
            PreparedStatement ps_get_latest_id = conn.prepareStatement(get_latest_id_query);
            PreparedStatement ps_genres_in_movies = conn.prepareStatement(insert_genres_in_movies_query);
            PreparedStatement ps_ratings = conn.prepareStatement(insert_rating);

            insert_all_genres(conn);


            ResultSet rs_getId = ps_get_latest_id.executeQuery();
            if (rs_getId.next()) latest_id = rs_getId.getString(1); else latest_id = "tt0000000";
            current_id = Integer.parseInt(latest_id.substring(2));


            int count = 0;
            for (Movie movie : movieList)
            {
                //inserting into movie table
                latest_id = "tt" + String.format("%07d",++current_id);

                movie.id = latest_id;
                ps_movies.setString(1, latest_id);
                ps_movies.setString(2,movie.title);
                ps_movies.setInt(3,movie.year);
                ps_movies.setString(4,movie.director);
                ps_movies.addBatch();

                ps_ratings.setString(1, latest_id);
                ps_ratings.addBatch();

                //----------------------------------------------------
                //inserting into genres_in_movies

                if (!movie.genres.isEmpty())

                {
                    for (String genre_name : movie.genres) //insert into genres_in_movies
                    {
                        ps_genres_in_movies.setString(1, latest_id);
                        ps_genres_in_movies.setString(2, genre_name);
                        ps_genres_in_movies.addBatch();
                    }

                }

                if (++count % batch_size == 0) //optimization
                {
                    ps_movies.executeBatch();
                    ps_movies.clearBatch();

                    ps_ratings.executeBatch();
                    ps_ratings.clearBatch();

                    ps_genres_in_movies.executeBatch();
                    ps_genres_in_movies.clearBatch();

                }

            }
            ps_ratings.addBatch();
            ps_movies.executeBatch();
            conn.setAutoCommit(true);
            stmt.execute("SET FOREIGN_KEY_CHECKS=1;");

        }
        catch (Exception e){e.printStackTrace();}

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if (qName.equalsIgnoreCase("film"))
        {
            current_movie = new Movie();
        }
    }



    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {

        switch (qName.toLowerCase()) {
            case "t": // title
                current_movie.title = element_content.trim();
                current_movie.director = current_director;
                break;

            case "year":
                try {
                    current_movie.year = Integer.parseInt(element_content.trim());
                } catch (Exception e)
                {
                    System.out.println("invalid year: " +  element_content.trim() + " movie: " + current_movie.title);
                    current_movie.year = 0; //TODO maybe change this to something else
                }
                break;

            case "cat": // single genre
                cats_in_movie.add(element_content.trim());
                unique_cats.add(element_content.trim());
                break;

            case "cats": // assign genres list
                current_movie.genres = cats_in_movie;
                cats_in_movie = new ArrayList<>();
                break;

            case "film": // add movie to list
                if (movieList.contains(current_movie))
                {
                    System.out.println("duplicate film: " + current_movie.title);
                }
                if (current_movie.year != 0 && !Objects.equals(current_movie.director, ""))
                    movieList.add(current_movie);
                break;

            case "dirname": // director name
                if (element_content.contains("Unknown") || element_content.contains("UnYear"))
                {
                    System.out.println("unknown director: " + element_content);
                    element_content = "";
                }

                current_director = element_content.trim();
                break;
        }


    } //function end




} //class end
