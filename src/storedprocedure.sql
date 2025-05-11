USE moviedb;

DELIMITER $$

CREATE PROCEDURE get_newId (IN oldId VARCHAR(10), OUT newId VARCHAR(10))
BEGIN
    DECLARE prefix VARCHAR(10);
    DECLARE numOnly INT;
    DECLARE newNum INT;

    SET prefix = SUBSTRING(oldId, 1, 2);
    SET numOnly = CAST(SUBSTRING(oldID, 3) AS UNSIGNED);
    SET newNum = numOnly + 1;
    SET newId = CONCAT(prefix, LPAD(newNum, 7, '0'));
END $$

CREATE PROCEDURE add_genre (IN genreName VARCHAR(32), OUT genreId INT)
BEGIN
    DECLARE existingId INT;

    SELECT id INTO existingId
    FROM genres
    WHERE name = genreName
    LIMIT 1;

    IF existingId IS NOT NULL THEN
        SET genreId = existingId;
    ELSE
        INSERT INTO genres (name)
        VALUES (genreName);

        SET genreId = LAST_INSERT_ID();
    END IF;

END $$

CREATE PROCEDURE find_star_by_name (IN starName VARCHAR(100), OUT starId VARCHAR(10))
BEGIN
    SELECT id INTO starId
    FROM stars
    WHERE name = starName
    LIMIT 1;
END $$

CREATE PROCEDURE add_star (IN starName VARCHAR(100), IN starBirthYear INT, OUT newId VARCHAR(10))
BEGIN
    DECLARE maxId VARCHAR(10);
    SELECT MAX(id) INTO maxId FROM stars;
    CALL get_newId(maxId, newId);

    INSERT INTO stars (id, name, birthYear)
    VALUES (newId, starName, starBirthYear);
END $$

CREATE PROCEDURE add_movie (
    IN movieTitle VARCHAR(100), IN movieYear INT, IN movieDirector VARCHAR(100),
    IN starName VARCHAR(100), IN starBirthYear INT, IN genreName VARCHAR(32),
    OUT newMovieId VARCHAR(10), OUT newStarId VARCHAR(10), OUT newGenreId INT
)
BEGIN
    add_movie: BEGIN
    DECLARE maxMovieId VARCHAR(10);
    DECLARE maxStarId VARCHAR(10);
    DECLARE foundStarId VARCHAR(10);
    DECLARE movieExists INT DEFAULT 0;

    SELECT COUNT(*) INTO movieExists
    FROM movies
    WHERE title = movieTitle AND year = movieyear AND director = movieDirector;

    IF movieExists > 0 THEN
        SELECT 'Movie already exists.' AS message;
        LEAVE add_movie;
    END IF;

    SELECT MAX(id) INTO maxMovieId FROM movies;
    CALL get_newId(maxMovieId, newMovieId);

    INSERT INTO movies (id, title, year, director)
    VALUES (newMovieId, movieTitle, movieYear, movieDirector);

    CALL find_star_by_name(starName, foundStarId);
    IF foundStarId IS NOT NULL THEN
        SET newStarId = foundStarId;
    ELSE
        SELECT MAX(id) INTO maxStarId FROM stars;
        CALL get_newId(maxStarId, newStarId);

        INSERT INTO stars (id, name, birthYear)
        VALUES (newStarId, starName, starBirthYear);
    END IF;

    INSERT INTO stars_in_movies (starId, movieId)
    VALUES (newStarId, newMovieId);

    CALL add_genre(genreName, newGenreId);

    INSERT INTO genres_in_movies (genreId, movieId)
    VALUES (newGenreId, newMovieId);

    SELECT CONCAT(
           'movieId: ', newMovieId, ', ',
           'starId: ', newStarId, ', ',
           'genreId: ', newGenreId, ', '
           ) AS message;
    END;
END $$

DELIMITER ;