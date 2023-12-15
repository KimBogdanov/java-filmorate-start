package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                "rating_id", film.getMpa().getId(),
                "likes", film.getLikes()
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
                "TITLE = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ? , RATING_ID = ?, LIKES = ?" +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getLikes(),
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
        String sqlFilms = "Select f.*, r.* FROM film as f left join RATING R on f.RATING_ID = R.RATING_ID where f.FILM_ID = ?";
        String sqlGenre = "SELECT fg.FILM_ID, g.* FROM FILM_GENRE AS fg LEFT JOIN GENRE AS g ON fg.GENRE_ID = g.GENRE_ID";
        Film film = jdbcTemplate.queryForObject(sqlFilms, getFilmRatingMapper(), id);
        List<Map<Long, Genre>> genres = jdbcTemplate.query(sqlGenre, getFilmGenreMapper());
        Long filmId = film.getId();
        for (Map<Long, Genre> longGenres : genres) {
            if (longGenres.containsKey(filmId)) {
                film.getGenres().add(longGenres.get(filmId));
            }
        }
        return film;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sqlFilms = "Select f.*, r.* FROM film as f left join RATING R on f.RATING_ID = R.RATING_ID ORDER BY f.likes DESC LIMIT ?";
        List<Film> films = jdbcTemplate.query(sqlFilms, getFilmRatingMapper(), count);
        return getFilms(films);
    }

    private List<Film> getFilms(List<Film> films) {
        String sqlGenre = "SELECT fg.FILM_ID, g.* FROM FILM_GENRE AS fg LEFT JOIN GENRE AS g ON fg.GENRE_ID = g.GENRE_ID";
        List<Map<Long, Genre>> genres = jdbcTemplate.query(sqlGenre, getFilmGenreMapper());
        for (Film film : films) {
            Long filmId = film.getId();
            for (Map<Long, Genre> longGenres : genres) {
                if (longGenres.containsKey(filmId)) {
                    film.getGenres().add(longGenres.get(filmId));
                }
            }
        }
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
        String sqlRate = "UPDATE film SET LIKES = (LIKES + 1) where film_id = ?";
        jdbcTemplate.update(sqlRate, filmId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_like WHERE (FILM_ID = ? and PERSON_ID = ?)";
        jdbcTemplate.update(sql, filmId, userId);
        String sqlRate = "update film set LIKES = (LIKES -1) where film_id = ?";
        jdbcTemplate.update(sqlRate, filmId);
    }

    private static RowMapper<Film> getFilmRatingMapper() {
        return (rs, num) -> new Film(rs.getLong("film_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getLong("duration"),
                new Rating(rs.getInt("rating_id"),
                        rs.getString("rating")),
                rs.getInt("likes"));
    }

    private static RowMapper<Map<Long, Genre>> getFilmGenreMapper() {
        return (rs, num) -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre"));
            return Map.of(filmId, genre);
        };
    }
}
