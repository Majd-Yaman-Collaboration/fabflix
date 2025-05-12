package XMLParsing;

import ObjectClasses.Movie;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.*;


//TODO
public class mains243 extends DefaultHandler
{
    Set<Movie> movieList = new HashSet<Movie>();
    ArrayList<String> cats_in_movie = new ArrayList<String>();
    Set<String> unique_cats = new HashSet<String>();
    String element_content;
    String current_director;
    private Movie current_movie;
    final int batch_size = 500;
    int current_id = 0;
    String latest_id;

    ArrayList<String> generated_ids = new ArrayList<>();


    public static void main(String[] args)
    {
        mains243 obj = new mains243();

        obj.parseDocument();

//        obj.movieList.stream().distinct().forEach(System.out::println);
        long x = obj.movieList.stream().count();
        System.out.println(x);

//        long start = System.currentTimeMillis();
//        obj.insert_all_movies();
//        long end = System.currentTimeMillis();
//        System.out.println("Time taken: " + (end - start)); //4136, 4352
//
//
//
//        Scanner input = new Scanner(System.in);
//        input.nextLine(); //pause the program for a moment
//
//        start = System.currentTimeMillis();
//        obj.delete_all_movies();
//        end = System.currentTimeMillis();
//        System.out.println("Time taken: " + (end - start));
    }

    private Connection get_connection() {
        String sql_connection_optimizations =
                "useSSL=false&rewriteBatchedStatements=true&useServerPrepStmts=false&useBatchMultiSend=true";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/moviedb?" + sql_connection_optimizations, //REWRITEBATCHED MAKES IT SO MUCH FASTER
                    "root",
                    "temporary123"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void insert_all_genres(Connection conn) throws Exception
    {
        String insert_genres_query = "INSERT INTO genres (name) VALUES (?);";
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

        String insert_genres_in_movies_query = "INSERT INTO genres_in_movies (genreId, movieId) " +
                "SELECT id, ? FROM genres WHERE name = ?";



        try (Connection conn = get_connection())
        {
            Statement stmt = conn.createStatement();
            stmt.execute("SET FOREIGN_KEY_CHECKS=0;"); //optimization
            conn.setAutoCommit(false);

            PreparedStatement ps_movies = conn.prepareStatement(insert_movies_query);
            PreparedStatement ps_get_latest_id = conn.prepareStatement(get_latest_id_query);

            PreparedStatement ps_genres_in_movies = conn.prepareStatement(insert_genres_in_movies_query);


            insert_all_genres(conn);


            ResultSet rs_getId = ps_get_latest_id.executeQuery();
            if (rs_getId.next()) latest_id = rs_getId.getString(1); else latest_id = "tt0000000";
            current_id = Integer.parseInt(latest_id.substring(2));


            int count = 0;
            for (Movie movie : movieList)
            {
                //inserting into movie table
                latest_id = "tt" + String.format("%07d",++current_id);
//                generated_ids.add(latest_id);

                movie.id = latest_id;
                ps_movies.setString(1, latest_id);
                ps_movies.setString(2,movie.title);
                ps_movies.setInt(3,movie.year);
                ps_movies.setString(4,movie.director);
                ps_movies.addBatch();
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
                    ps_genres_in_movies.executeBatch();
                    ps_genres_in_movies.clearBatch();
                }


                if (++count % batch_size == 0) //optimization
                {
                    ps_movies.executeBatch();
                    ps_movies.clearBatch();

                }

            }

            ps_movies.executeBatch();
            conn.setAutoCommit(true);
            stmt.execute("SET FOREIGN_KEY_CHECKS=1;");

        }
        catch (Exception e){e.printStackTrace();}

    }

    private void delete_all_movies()
    {
        String delete_movies_query = "DELETE FROM movies WHERE id = ?";
        String delete_genres_query = "DELETE FROM genres WHERE name = ?";

        PreparedStatement delete_movies_ps;
        try (Connection conn = get_connection())
        {
            conn.setAutoCommit(false);
            delete_movies_ps = conn.prepareStatement(delete_movies_query);

            int count = 0;
            for (Movie movie : movieList)
            {
                delete_movies_ps.setString(1,movie.id);
                delete_movies_ps.addBatch();

                if (++count % batch_size == 0)
                {
                    delete_movies_ps.executeBatch();
                    delete_movies_ps.clearBatch();
                }
            }
            delete_movies_ps.executeBatch();

            PreparedStatement delete_genres_ps = conn.prepareStatement(delete_genres_query);
            for (String genre_name : unique_cats)
            {
                delete_genres_ps.setString(1, genre_name);
                delete_genres_ps.addBatch();
            }
            delete_genres_ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        }
        catch (Exception e){e.printStackTrace();}
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //get a new instance of parser
            FileInputStream fis = new FileInputStream("mains243.xml");
            InputStreamReader isr = new InputStreamReader(fis, "ISO-8859-1");
            InputSource is = new InputSource(isr);
            is.setEncoding("ISO-8859-1");
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(is, this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
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
    public void characters(char[] ch, int start, int length) throws SAXException {
        element_content = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {

        switch (qName.toLowerCase()) {
            case "t": // title
                current_movie.title = element_content;
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
                cats_in_movie.add(element_content);
                unique_cats.add(element_content);
                break;

            case "cats_in_movie": // assign genres list
                current_movie.genres = cats_in_movie;
                cats_in_movie = new ArrayList<>();
                break;

            case "film": // add movie to list
                if (movieList.contains(current_movie))
                {
                    System.out.println("duplicate film: " + current_movie.title);
                }
                if (current_movie.year != 0)
                    movieList.add(current_movie);
                break;

            case "dirname": // director name
                current_director = element_content;
                break;
        }


    } //function end




} //class end
