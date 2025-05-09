USE moviedb;

DELIMITER $$
/*
CREATE PROCEDURE add_movie (IN title, IN releaseYear, IN director, IN star, IN birthYear, IN genre)
BEGIN
    INSERT
END $$
 */

CREATE PROCEDURE add_star (IN starName VARCHAR(100), IN starBirthYear INT)
BEGIN
    DECLARE maxId VARCHAR(10);
    DECLARE numOnly INT;
    DECLARE newNum INT;
    DECLARE newId VARCHAR(10);

    SELECT MAX(id) INTO maxId FROM stars;
    SET numOnly = CAST(SUBSTRING(maxID, 3), AS UNSIGNED);
    SET newNum = numOnly + 1;
    SET newId = CONCAT('nm', LPAD(newNum, 7, '0');

    INSERT INTO stars (id, name, birthYear)
    VALUES (newId, starName, starBirthYear);
END $$

DELIMITER ; 

/*
get max id
get substring of id for only numbers
cast it into int
increment int
cast back to varchar
combine first 2 varchar with numbers varchar
insert into stars
 */