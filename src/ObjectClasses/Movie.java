package ObjectClasses;

public class Movie
{
    public final String id;

    public final String title;

    public final String year;

    public final String director;


    public Movie(String id, String title, String year, String director)
    {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
    }

    @Override
    public String toString() {
        return  "id "       + id    + "|" +
                "title "    + title + "|" +
                "year "     + year  + "|" +
                "director " + director;
    }

    public void print()
    {
        System.out.println(toString());
    }
}
