package XMLParsing;


import ObjectClasses.Actor;
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

public class casts124 extends BaseXMLParsing
{
    Set<Actor> actorList = new HashSet<>();
    private String current_director;
    private Actor current_actor;


    public static void main(String[] args)
    {
        casts124 obj = new casts124();
        obj.parseDocument();
        long x = obj.actorList.size();
        System.out.println(x);
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if (qName.equalsIgnoreCase("filmc"))
        {
            current_actor = new Actor();
        }
    }
    public void endElement(String uri, String localName, String qName) throws SAXException
    {

        switch (qName.toLowerCase())
        {
            case "is": //is -> director name
                current_director = element_content;
                break;
            case "t": //t -> movie title
                current_actor.title = element_content;
                break;
            case "a": //a -> actor name
                current_actor.name = element_content;
                current_actor.director = current_director;
                actorList.add(current_actor);
                break;
        }


    } //function end





}
