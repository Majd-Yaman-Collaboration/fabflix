DROP DATABASE IF EXISTS moviedb;
CREATE DATABASE moviedb;
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'password';

GRANT ALL PRIVILEGES ON moviedb.* TO 'admin'@'localhost';

FLUSH PRIVILEGES;

CREATE TABLE movies(
                       id VARCHAR(10) PRIMARY KEY,
                       title VARCHAR(100) NOT NULL,
                       year INTEGER NOT NULL,
                       director VARCHAR(100) NOT NULL
);

CREATE TABLE stars(
                      id VARCHAR(10) PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      birthYear INTEGER
);

CREATE TABLE stars_in_movies(
                                starId VARCHAR(10),
                                movieId VARCHAR(10),
                                PRIMARY KEY (starId,movieId),
                                FOREIGN KEY starId REFERENCES stars(id) ON DELETE CASCADE,
                                FOREIGN KEY movieId REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE genres(
                       id INTEGER AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(32) NOT NULL
);

CREATE TABLE genres_in_movies(
                                 genreId INTEGER,
                                 movieId VARCHAR(10),
                                 PRIMARY KEY (genreId,movieId),
                                 FOREIGN KEY genreId REFERENCES genres(id) ON DELETE CASCADE,
                                 FOREIGN KEY movieId REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE customers(
                          id INTEGER AUTO_INCREMENT PRIMARY KEY,
                          firstName VARCHAR(50),
                          lastName VARCHAR(50),
                          ccid VARCHAR(20),
                          address VARCHAR(200),
                          email VARCHAR(50),
                          password VARCHAR(20),
                          FOREIGN KEY ccid REFERENCES creditcards(id) ON DELETE CASCADE
);

CREATE TABLE sales(
                      id INTEGER AUTO_INCREMENT PRIMARY KEY,
                      customerId INTEGER,
                      movieId VARCHAR(10),
                      saleDate DATE,
                      FOREIGN KEY customerId REFERENCES customers(id) ON DELETE CASCADE,
                      FOREIGN KEY movieId REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE creditcards(
                            id VARCHAR(20) PRIMARY KEY,
                            firstName VARCHAR(50),
                            lastName VARCHAR(50),
                            expiration DATE
);

CREATE TABLE ratings(
                        movieId VARCHAR(10) PRIMARY KEY,
                        rating FLOAT,
                        numVotes INTEGER,
                        FOREIGN KEY movieId REFERENCES movies(id) ON DELETE CASCADE\
);