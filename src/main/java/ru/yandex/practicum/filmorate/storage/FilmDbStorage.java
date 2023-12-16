package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.*;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilms() {
        String sqlFilms = "Select f.*, r.* FROM film as f left join RATING R on f.RATING_ID = R.RATING_ID";
        List<Film> films = jdbcTemplate.query(sqlFilms, getFilmRatingMapper());
        return getFilms(films);
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        Long id = insert.executeAndReturnKey(filmToMap(film)).longValue();
        film.setId(id);
        if (!film.getGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            saveGenresToFilm(id, genres);
        }
        return film;
    }

    private Map<String, Object> filmToMap(Film film) {
        return Map.of(
                "title", film.getName(),
                "description", film.getDescription(),
                "release_date", Date.valueOf(film.getReleaseDate()),
                "duration", (int) film.getDuration(),
                "rating_id", film.getMpa().getId()
        );
    }

    private void saveGenresToFilm(Long id, List<Genre> genres) {
        jdbcTemplate.batchUpdate("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, id);
                        ps.setInt(2, genres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }

    @Override
    public Film updateFilm(Film film) {
        isExist(film.getId());
        String sqlQuery = "UPDATE film SET " +
                "TITLE = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ? , RATING_ID = ?" +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        String sqlDelete = "DELETE FROM film_genre WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlDelete, film.getId());
        if (!film.getGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            saveGenresToFilm(film.getId(), genres);
        }
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        String sqlFilms = "Select f.*, r.* " +
                "FROM film as f " +
                "LEFT JOIN RATING R on f.RATING_ID = R.RATING_ID " +
                "WHERE f.FILM_ID = ?";
        String sqlGenre = "SELECT fg.FILM_ID, g.* " +
                "FROM FILM_GENRE AS fg " +
                "LEFT JOIN GENRE AS g ON fg.GENRE_ID = g.GENRE_ID WHERE fg.FILM_ID = ?";
        Film film = jdbcTemplate.queryForObject(sqlFilms, getFilmRatingMapper(), id);
        List<Pair<Long, Genre>> genres = jdbcTemplate.query(sqlGenre, getFilmGenreMapper(), id);
        Long filmId = film.getId();
        for (Pair<Long, Genre> longGenres : genres) {
            if (longGenres.getLeft().equals(filmId)) {
                film.getGenres().add(longGenres.getRight());
            }
        }
        return film;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sqlFilms = "Select f.*, r.*  " +
                "FROM film as f " +
                "LEFT JOIN RATING R on f.RATING_ID = R.RATING_ID " +
                "LEFT JOIN FILM_LIKE FL on f.FILM_ID = FL.FILM_ID " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.PERSON_ID) " +
                "DESC LIMIT ?";
        List<Film> films = jdbcTemplate.query(sqlFilms, getFilmRatingMapper(), count);
        return getFilms(films);
    }

    private List<Film> getFilms(List<Film> films) {
        String sqlGenre = "SELECT fg.FILM_ID, g.* FROM FILM_GENRE AS fg LEFT JOIN GENRE AS g ON fg.GENRE_ID = g.GENRE_ID";
        List<Pair<Long, Genre>> genres = jdbcTemplate.query(sqlGenre, getFilmGenreMapper());

        Map<Long, List<Genre>> genresByFilmId = genres.stream()
                .collect(Collectors.groupingBy(Pair::getLeft,
                        Collectors.mapping(Pair::getRight, Collectors.toList())));

        films.forEach(film -> film.getGenres()
                .addAll(genresByFilmId.getOrDefault(film.getId(), Collections.emptyList())));

        return films;
    }


    @Override
    public boolean isExist(Long id) {
        String sql = "select * from FILM where FILM_ID = ?";
        return !jdbcTemplate.queryForRowSet(sql, id).next();
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO FILM_LIKE (FILM_ID, PERSON_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_like WHERE (FILM_ID = ? and PERSON_ID = ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private static RowMapper<Film> getFilmRatingMapper() {
        return (rs, num) -> new Film(rs.getLong("film_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getLong("duration"),
                new Rating(rs.getInt("rating_id"),
                        rs.getString("rating")));
    }

    private static RowMapper<Pair<Long, Genre>> getFilmGenreMapper() {
        return (rs, num) -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre"));
            return Pair.of(filmId, genre);
        };
    }
}
