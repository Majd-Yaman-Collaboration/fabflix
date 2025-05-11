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
import java.util.ArrayList;
import java.util.Scanner;


//TODO
public class mains243 extends DefaultHandler
{
    ArrayList<Movie> movieList = new ArrayList<Movie>();
    ArrayList<String> cats = new ArrayList<String>();
    String element_content;
    String current_director;
    private Movie current_movie;

    int current_id = 0;
    String max;
    PreparedStatement ps;
    ArrayList<String> generated_ids = new ArrayList<>();


    public static void main(String[] args)
    {
        mains243 obj = new mains243();
        obj.parseDocument();

        obj.insert_all_movies();

        Scanner input = new Scanner(System.in);
        input.nextLine(); //pause the program for a moment

        obj.delete_all_movies();


    }

    private Connection get_connection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/moviedb",
                    "root",
                    "temporary123"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void insert_all_movies()
    {
        String insert_query = "INSERT INTO movies VALUES (?,?,?,?)";
        String get_max_id_query = "SELECT MAX(id) FROM movies";

        try (Connection conn = get_connection())
        {
            assert conn != null;
            conn.setAutoCommit(false);
            PreparedStatement ps_getId = conn.prepareStatement(get_max_id_query);
            ps = conn.prepareStatement(insert_query);
            ResultSet rs_getId = ps_getId.executeQuery();

            if (rs_getId.next()) max = rs_getId.getString(1); else max = "tt0000000";
            current_id = Integer.parseInt(max.substring(2));

            int batch_size = 500;
            int count = 0;
            for (Movie movie : movieList)
            {
                ++current_id;
                max = "tt" + String.format("%07d",current_id);
                generated_ids.add(max);


                ps.setString(1,max);
                ps.setString(2,movie.title);
                ps.setInt(3,movie.year);
                ps.setString(4,movie.director);
                ps.addBatch();

                if (++count % batch_size == 0)
                {
                    ps.executeBatch();
                    ps.clearBatch();
                }

            }

            ps.executeBatch();
            conn.setAutoCommit(true);
        }
        catch (Exception e){e.printStackTrace();}

    }

    private void delete_all_movies()
    {
        String query = "DELETE FROM movies WHERE id = ?";

        try (Connection conn = get_connection())
        {
            ps = conn.prepareStatement(query);
            conn.setAutoCommit(false);
            for (String id : generated_ids)
            {
                ps.setString(1,id);
                ps.addBatch();
            }
            ps.executeBatch();
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
                    current_movie.year = Integer.parseInt(element_content);
                } catch (Exception e)
                {
                    current_movie.year = 0;
                }
                break;

            case "cat": // single genre
                cats.add(element_content);
                break;

            case "cats": // assign genres list
                current_movie.genres = cats;
                cats = new ArrayList<>();
                break;

            case "film": // add movie to list
                movieList.add(current_movie);
                break;

            case "dirname": // director name
                current_director = element_content;
                break;
        }


    } //function end




} //class end
