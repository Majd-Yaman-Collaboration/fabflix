package XMLParsing;

import ObjectClasses.Movie;
import jakarta.servlet.http.HttpServlet;
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
import java.util.ArrayList;
import java.util.List;

public class mains243 extends DefaultHandler
{
    ArrayList<Movie> movieList = new ArrayList<Movie>();
    ArrayList<String> cats = new ArrayList<String>();
    String element_content;
    String current_director;
    private Movie current_movie;


    public static void main(String[] args)
    {
        mains243 obj = new mains243();
        obj.parseDocument();
        obj.movieList.stream().skip(100).limit(20).forEach(System.out::println);
        System.out.println(obj.movieList.size());
        System.out.println(obj.movieList.stream().filter(Movie::valid).count());
        obj.movieList.stream().filter(movie -> {
            return !movie.valid();
        }).forEach(System.out::println);


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

        if (qName.equalsIgnoreCase("t")) //title
        {

            current_movie.title = element_content;
            current_movie.director = current_director;
        }
        else if (qName.equalsIgnoreCase("fid") || qName.equalsIgnoreCase("filmed"))
        {
            current_movie.id = element_content;
        }
        else if (qName.equalsIgnoreCase("year"))
        {
            current_movie.year = element_content;
        }
        else if (qName.equalsIgnoreCase("cat")) //single genre (category)
        {
            cats.add(element_content);
        }
        else if (qName.equalsIgnoreCase("cats")) //genres (categories)
        {
            current_movie.genres = cats;
            cats = new ArrayList<>();
            movieList.add(current_movie);
        }
        else if (qName.equalsIgnoreCase("dirname"))
        {
            current_director = element_content;

        }

    } //function end




} //class end
