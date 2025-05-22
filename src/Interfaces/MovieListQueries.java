package Interfaces;

public interface MovieListQueries
{
    //if genre
    String genreQuery =
            "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating " +
            "FROM movies m " +
            "JOIN ratings r ON m.id = r.movieId " +
            "JOIN genres_in_movies gm ON m.id = gm.movieId " +
            "JOIN genres g ON gm.genreId = g.id " +
            "WHERE g.name = ? ";
            //Now placed in through the java servlet
           /* "ORDER BY ?" + //ORDER BY X ASC/DESC, Y ASC/DESC
            " LIMIT ? OFFSET ?";*/
    String genreCountQuery =
            "SELECT COUNT(DISTINCT m.id) as total " +
            "FROM movies m " +
            "JOIN ratings r ON m.id = r.movieId " +
            "JOIN genres_in_movies gm ON m.id = gm.movieId " +
            "JOIN genres g ON gm.genreId = g.id " +
            "WHERE g.name = ?";


    //else if title // if '*'
    String titleAsteriskQuery =
            "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating " +
            "FROM movies m " +
            "JOIN ratings r ON m.id = r.movieId " +
            "WHERE NOT REGEXP_LIKE(m.title, '^[A-Za-z0-9]') ";
    String titleAsteriskCountQuery =
            "SELECT COUNT(DISTINCT m.id) as total " +
            "FROM movies m " +
            "JOIN ratings r ON m.id = r.movieId " +
            "WHERE NOT REGEXP_LIKE(m.title, '^[A-Za-z0-9]')";


    //else normal regexp
    String titleRegexpQuery =
            "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating " +
            "FROM movies m " +
            "JOIN ratings r ON m.id = r.movieId " +
            "WHERE UPPER(m.title) LIKE ? ";
    String titleRegexpCountQuery =
            "SELECT COUNT(DISTINCT m.id) as total " +
            "FROM movies m " +
            "JOIN ratings r ON m.id = r.movieId " +
            "WHERE UPPER(m.title) LIKE ?";

    //search query
    String searchQuery =
            "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating " +
            "FROM movies m " +
            "JOIN ratings r on m.id = r.movieID " +
            "JOIN stars_in_movies sim on m.id = sim.movieID " +
            "JOIN stars s ON s.id = sim.starID " +
            "WHERE MATCH(m.title) AGAINST(? IN BOOLEAN MODE) " +
            "AND (-1 = ? OR m.year = ?) " + //-1 is sent by us to say the user didn't input anything
            "AND UPPER(m.director) LIKE ? " +
            "AND UPPER(s.name) LIKE ? ";

    String searchCountQuery =
            "SELECT COUNT(DISTINCT m.id) AS total " +
            "FROM movies m " +
            "JOIN ratings r ON m.id = r.movieID " +
            "JOIN stars_in_movies sim ON m.id = sim.movieID " +
            "JOIN stars s ON s.id = sim.starID " +
            "WHERE MATCH(m.title) AGAINST(? IN BOOLEAN MODE) " +
            "AND (-1 = ? OR m.year = ?) " + //see comment above
            "AND UPPER(m.director) LIKE ? " +
            "AND UPPER(s.name) LIKE ? ";



    //else standard
    String standardQuery =
            "SELECT m.id, m.title, m.year, m.director, r.rating " +
            "FROM movies m JOIN ratings r ON m.id = r.movieId ";
    String standardCountQuery =
            "SELECT COUNT(*) as total " +
            "FROM movies m JOIN ratings r ON m.id = r.movieId";

    //
}