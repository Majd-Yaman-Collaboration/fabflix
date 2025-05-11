package XMLParsing;

import ObjectClasses.Movie;
import jakarta.servlet.http.HttpServlet;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class mains243 extends DefaultHandler
{
    List<Movie> movieList = new ArrayList<Movie>();

    public static void main(String[] args)
    {
        mains243 obj = new mains243();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();
            //parse the file and also register this class for call backs
            sp.parse("employees.xml", this);

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
        if (qName.equalsIgnoreCase("fid"))
        {

        }
    }

}
