package ObjectClasses;

import java.util.ArrayList;

public class Movie
{
    public  String id = "";

    public  String title = "";

    public  String year = "";

    public  String director = "";

    public ArrayList<String> genres;

    public boolean valid()
    {
        return !(id.isEmpty() || title.isEmpty() || year.isEmpty() || director.isEmpty());
    }

    public Movie()
    {}

    @Override
    public String toString() {
        return  "id "       + id       + "|" +
                "title "    + title    + "|" +
                "year "     + year     + "|" +
                "director " + director + "|" +
                "genre "    + genres;
    }
}
