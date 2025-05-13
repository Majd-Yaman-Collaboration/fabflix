package XMLParsing;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.sql.*;
import java.util.*;



public class mains243 extends BaseXMLParsing
{
    Set<Movie> movieList = new HashSet<Movie>();
    ArrayList<String> cats_in_movie = new ArrayList<String>();
    Set<String> unique_cats = new HashSet<String>();
//    String element_content; now in the SuperClass
    String current_director;
    private Movie current_movie;
    final int batch_size = 500;
    int current_id = 0;
    String latest_id;

    int unkown_directors = 0;
    int duplicate_films = 0;
    int invalid_years = 0;



    public void run_mains243(String filename)
    {
        long start = System.currentTimeMillis();
        System.out.println("Creating main243 objects...");
        mains243 obj = new mains243();

        System.out.println("Parsing document");
        obj.parseDocument(filename);

        System.out.println("Inserting into the database");
        obj.insert_all_movies();
        long end = System.currentTimeMillis();
        System.out.println("Time taken: " + (end - start)); //
        System.out.println("Number of invalid directors: " + obj.unkown_directors);
        System.out.println("Number of duplicate films: " + obj.duplicate_films);
        System.out.println("Number of invalid years: " + obj.invalid_years);
    }


    private void insert_all_genres(Connection conn) throws Exception
    {
        String insert_genres_query = "INSERT IGNORE INTO genres (name) VALUES (?);";
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

        String insert_genres_in_movies_query = "INSERT IGNORE INTO genres_in_movies (genreId, movieId) " +
                "SELECT id, ? FROM genres WHERE name = ?";

        //ID, RATING, NUMVOTES
        String insert_rating = "INSERT INTO ratings VALUES (?, 0.0, 0)";



        try (Connection conn = get_connection())
        {
            Statement stmt = conn.createStatement();
            stmt.execute("SET FOREIGN_KEY_CHECKS=0;"); //optimization
            conn.setAutoCommit(false);

            PreparedStatement ps_movies = conn.prepareStatement(insert_movies_query);
            PreparedStatement ps_get_latest_id = conn.prepareStatement(get_latest_id_query);
            PreparedStatement ps_genres_in_movies = conn.prepareStatement(insert_genres_in_movies_query);
            PreparedStatement ps_ratings = conn.prepareStatement(insert_rating);

            insert_all_genres(conn);


            ResultSet rs_getId = ps_get_latest_id.executeQuery();
            if (rs_getId.next()) latest_id = rs_getId.getString(1); else latest_id = "tt0000000";
            current_id = Integer.parseInt(latest_id.substring(2));


            int count = 0;
            for (Movie movie : movieList)
            {
                //inserting into movie table
                latest_id = "tt" + String.format("%07d",++current_id);

                movie.id = latest_id;
                ps_movies.setString(1, latest_id);
                ps_movies.setString(2,movie.title);
                ps_movies.setInt(3,movie.year);
                ps_movies.setString(4,movie.director);
                ps_movies.addBatch();

                ps_ratings.setString(1, latest_id);
                ps_ratings.addBatch();

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

                }

                if (++count % batch_size == 0) //optimization
                {
                    System.out.println("Executing batches");
                    ps_movies.executeBatch();
                    ps_movies.clearBatch();

                    ps_ratings.executeBatch();
                    ps_ratings.clearBatch();

                    ps_genres_in_movies.executeBatch();
                    ps_genres_in_movies.clearBatch();

                }

            }
            ps_ratings.addBatch();
            ps_movies.executeBatch();
            conn.setAutoCommit(true);
            stmt.execute("SET FOREIGN_KEY_CHECKS=1;");

        }
        catch (Exception e){e.printStackTrace();}

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
    public void endElement(String uri, String localName, String qName) throws SAXException
    {

        switch (qName.toLowerCase()) {
            case "t": // title
                current_movie.title = element_content.trim();
                current_movie.director = current_director;
                break;

            case "year":
                try {
                    current_movie.year = Integer.parseInt(element_content.trim());
                } catch (Exception e)
                {
                    ++invalid_years;
                    System.out.println("BAD YEAR -> title: " + current_movie.title + " Director: " + current_movie.director + " Year: " + element_content);
                    current_movie.year = 0;
                }
                break;

            case "cat": // single genre
                element_content = closest_genre(element_content.trim().toLowerCase());
                cats_in_movie.add(element_content.trim());
                unique_cats.add(element_content.trim());
                break;

            case "cats": // assign genres list
                current_movie.genres = cats_in_movie;
                cats_in_movie = new ArrayList<>();
                break;

            case "film": // add movie to list
                if (movieList.contains(current_movie))
                {
                    ++duplicate_films;
                    System.out.println("DUPLICATE FILM -> title: " + current_movie.title + " Director: " + current_movie.director + " Year: " + current_movie.year);
                }
                if (current_movie.year != 0 && !Objects.equals(current_movie.director, ""))
                    movieList.add(current_movie);
                break;

            case "dirname": // director name
                if (element_content.contains("Unknown") || element_content.contains("UnYear"))
                {
                    ++unkown_directors;
                    System.out.println("UNKNOWN DIRECTORS -> title: " + current_movie.title + " Director: " + current_movie.director + " Year: " + current_movie.year);
                    element_content = "";
                }

                current_director = element_content.trim();
                break;
        }


    } //function end



    private String closest_genre(String given_genre)
    {                         //passed in as lowercase
        switch (given_genre)
        {
            case "act" :
            case "actn":
            case "axtn":
                given_genre = "Action";
                break;
            case "adct":
            case "adctx":
                given_genre = "Adult Content";
                break;
            case "advt":
                given_genre = "Adventure";
                break;
            case "anti-dram":
                given_genre = "Anti-Drama";
                break;
            case "avga":
                given_genre = "Avant Garde";
                break;
            case "biob":
                given_genre = "Biographical Biography";
                break;
            case "biog":
                given_genre = "Biography";
                break;
            case "biop":
            case "biopp":
            case "biopx":
                given_genre = "Biographical Picture";
                break;
            case "bnw TV":
                given_genre = "Black and White TV";
                break;
            case "ca":
                given_genre = "Children's Adventure";
                break;
            case "cart":
                given_genre = "Cartoon";
                break;
            case "cmr":
            case "romt comd":
            case "romt. comd":
                given_genre = "Comedy Romance";
                break;
            case "cnr":
            case "cnrb":
            case "cnrbb":
                given_genre = "Crime and Robbery";
                break;
            case "col tv":
                given_genre = "Color TV";
                break;
            case "comd":
            case "comdx":
                given_genre = "Comedy";
                break;
            case "comd noir":
            case "noir comd":
                given_genre = "Noir Comedy";
                break;
            case "comd west":
                given_genre = "Western Comedy";
                break;
            case "crim":
                given_genre = "Criminal";
                break;
            case "ctcxx":
            case "ctxx":
            case "ctxxx":
            case "cult": //cult or culture
            case "dist":
            case "exmp":
            case "homo":
            case "rfp; h*":
            case "scat":
                //not sure about them so lowk just break;
                break;
            case "disa":
                given_genre = "Disaster";
                break;
            case "docu":
            case "duco":
            case "ducu":
                given_genre = "Documentary";
                break;
            case "docu dram":
            case "dram docu":
                given_genre = "Drama Documentary";
                break;
            case "dram":
            case "dram>":
            case "dramd":
            case "dramn":
            case "ram":
                given_genre = "Drama";
                break;
            case "dram.actn":
                given_genre = "Action Drama";
                break;
            case "faml":
                given_genre = "Family";
                break;
            case "fant":
                given_genre = "Fantasy";
                break;
            case "fanth*":
                given_genre = "Fantasy Horror *";
                break;
            case "h":
            case "hor":
            case "horr":
                given_genre = "Horror";
                break;
            case "h**":
                given_genre = "Horror **";
                break;
            case "hist":
                given_genre = "History";
                break;
            case "musc":
            case "muscl":
            case "muusc":
                given_genre = "Musical";
                break;
            case "myst":
                given_genre = "Mystery";
                break;
            case "natu":
                given_genre = "Nature";
                break;
            case "noir comd romt":
                given_genre = "Noir Comedy Romance";
                break;
            case "psyc":
                given_genre = "Psychological";
                break;
            case "psych drama":
                given_genre = "Psychological Drama";
                break;
            case "romt":
            case "romtx":
            case "ront":
                given_genre = "Romance";
                break;
            case "romt actn":
                given_genre = "Romantic Action";
                break;
            case "romt dram":
                given_genre = "Romantic Drama";
                break;
            case "romt fant":
                given_genre = "Romantic Fantasy";
                break;
            case "romtadvt":
                given_genre = "Romantic Adventure";
                break;
            case "s.f.":
            case "scfi":
            case "scif":
            case "sctn":
            case "sxfi":
                given_genre = "Sci-fi";
                break;
            case "sati":
                given_genre = "Satire";
                break;
            case "sport":
                given_genre = "Sports";
                break;
            case "surl":
            case "surr":
                given_genre = "Surreal";
                break;
            case "viol":
                given_genre = "Violence";
                break;
            case "west":
            case "west1":
                given_genre = "Western";

        }

        return given_genre;
    }



} //class end
