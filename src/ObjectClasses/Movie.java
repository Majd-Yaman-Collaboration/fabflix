package ObjectClasses;

import java.util.ArrayList;
import java.util.Objects;

public class Movie
{
    public  String id = "";

    public  String title = "";

    public int   year = 0;

    public  String director = "";

    public double rating = 0;

    public ArrayList<String> genres = new ArrayList<>();

    //simply for testing and making sure I got everything
    public boolean valid()
    {
        return !(title.isEmpty() || year == 0 || director.isEmpty() || rating == 0 || id.isEmpty());
    }

    public Movie()
    {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return year == movie.year &&
                title.equals(movie.title) &&
                director.equals(movie.director);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, director, year);
    }


    @Override
    public String toString() {
        return  "id "       + id       + "|" +
                "title "    + title    + "|" +
                "year "     + year     + "|" +
                "director " + director + "|" +
                "genre "    + genres   + "|" +
                "rating  "  + rating;
    }
}
