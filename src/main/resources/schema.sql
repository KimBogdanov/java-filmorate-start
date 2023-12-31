DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS film_like;
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS film;
DROP TABLE IF EXISTS rating;
DROP TABLE IF EXISTS person;
CREATE TABLE IF NOT EXISTS person(
    person_id     INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email         varchar(100) ,
    login         varchar(50),
    name          varchar(40),
    birthday      timestamp(8),
    CONSTRAINT uq_email UNIQUE (email));

CREATE TABLE IF NOT EXISTS rating(
    rating_id     integer GENERATED BY DEFAULT AS IDENTITY UNIQUE,
    rating        varchar(100) UNIQUE,
    PRIMARY KEY (rating_id, rating));

CREATE TABLE IF NOT EXISTS film(
    film_id       integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title         varchar(200),
    description   varchar(1000),
    release_date  timestamp,
    duration      integer,
    rating_id     integer REFERENCES rating(rating_id));

CREATE TABLE IF NOT EXISTS genre(
    genre_id      integer GENERATED BY DEFAULT AS IDENTITY UNIQUE,
    genre         varchar(100) UNIQUE,
    PRIMARY KEY (genre_id, genre));

CREATE TABLE IF NOT EXISTS film_genre(
    film_id       integer REFERENCES film(film_id),
    genre_id      integer REFERENCES genre(genre_id),
    CONSTRAINT film_genre UNIQUE (film_id, genre_id));

CREATE TABLE IF NOT EXISTS film_like(
    person_id     integer REFERENCES person(person_id),
    film_id       integer REFERENCES film (film_id),
    CONSTRAINT person_film UNIQUE (person_id, film_id));

CREATE TABLE IF NOT EXISTS friends(
    person_id     integer REFERENCES person(person_id),
    friends_id    integer REFERENCES person(person_id),
    CONSTRAINT person_friends UNIQUE (person_id, friends_id));