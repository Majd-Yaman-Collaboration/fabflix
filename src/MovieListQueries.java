public interface MovieListQueries
{
    //if genre
    String genreQuery =
            "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating " +
            "FROM movies m " +
            "JOIN ratings r ON m.id = r.movieId " +
            "JOIN genres_in_movies gm ON m.id = gm.movieId " +
            "JOIN genres g ON gm.genreId = g.id " +
            "WHERE g.name = ? " +
            "ORDER BY r.rating DESC LIMIT ? OFFSET ?";
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
            "WHERE NOT REGEXP_LIKE(m.title, '^[A-Za-z0-9]') " +
            "ORDER BY r.rating DESC LIMIT ? OFFSET ?";
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
            "WHERE UPPER(m.title) LIKE ? " +
            "ORDER BY r.rating DESC LIMIT ? OFFSET ?";

    String titleRegexpCountQuery =
            "SELECT COUNT(DISTINCT m.id) as total " +
            "FROM movies m " +
            "JOIN ratings r ON m.id = r.movieId " +
            "WHERE UPPER(m.title) LIKE ?";

    //else standard
    String standardQuery =
            "SELECT m.id, m.title, m.year, m.director, r.rating " +
            "FROM movies m JOIN ratings r ON m.id = r.movieId " +
            "ORDER BY r.rating DESC LIMIT ? OFFSET ?";
    String standardCountQuery =
            "SELECT COUNT(*) as total " +
            "FROM movies m JOIN ratings r ON m.id = r.movieId";

    //
}