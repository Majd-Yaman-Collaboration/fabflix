package ObjectClasses;

import java.util.ArrayList;

public class Movie
{

    public  String title = "";

    public int   year = 0;

    public  String director = "";

    public double rating = 0;

    public ArrayList<String> genres;

    //simply for testing and making sure I got everything
    public boolean valid()
    {
        return !(title.isEmpty() || year == 0 || director.isEmpty() || rating == 0);
    }

    public Movie()
    {}

    @Override
    public String toString() {
        return
                "title "    + title    + "|" +
                "year "     + year     + "|" +
                "director " + director + "|" +
                "genre "    + genres   + "|" +
                "rating  "  + rating;
    }
}
