package star.XMLParsing;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;

public abstract class BaseXMLParsing extends DefaultHandler
{
    protected String element_content;

    protected void parseDocument(String fileName) {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            //get a new instance of parser
            FileInputStream fis = new FileInputStream(fileName);
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


    protected Connection get_connection() {
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

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        element_content = new String(ch, start, length);
    }

}
