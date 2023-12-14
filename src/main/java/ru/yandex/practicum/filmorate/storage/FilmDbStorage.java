package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
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
        String sql = "SELECT f.*, g.* , r.RATING " +
                "FROM film f " +
                "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
                "JOIN RATING AS r ON f.RATING_ID = r.RATING_ID";
        List<DualElement<Film, Genre>> rowList = jdbcTemplate.query(sql, getFilmTempMapper());
        Film film = new Film();
        List<Genre> genres = new ArrayList<>();
        return getFilms(rowList, film, genres);
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
            saveFilmGenre(id, genres);
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

    private void saveFilmGenre(Long id, List<Genre> genres) {
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
            saveFilmGenre(film.getId(), genres);
        }
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "SELECT f.*, g.* , r.RATING " +
                "FROM film f " +
                "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
                "JOIN RATING AS r ON f.RATING_ID = r.RATING_ID  where f.FILM_ID = ?";
        List<DualElement<Film, Genre>> rowList = jdbcTemplate.query(sql, getFilmTempMapper(), id);
        Film film = null;
        List<Genre> genres = new ArrayList<>();
        for (DualElement<Film, Genre> filmGenre : rowList) {
            if (film == null) {
                film = filmGenre.getFirst();
            }
            if (filmGenre.getSecond().getName() == null) {
                return film;
            }
            genres.add(filmGenre.getSecond());
        }
        film.setGenres(genres);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sql = "SELECT f.*, g.* , r.RATING " +
                "FROM film f " +
                "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
                "JOIN RATING AS r ON f.RATING_ID = r.RATING_ID " +
                "ORDER BY f.likes DESC LIMIT ?";
        List<DualElement<Film, Genre>> rowList = jdbcTemplate.query(sql, getFilmTempMapper(), count);
        Film film = new Film();
        List<Genre> genres = new ArrayList<>();
        return getFilms(rowList, film, genres);
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

    private List<Film> getFilms(List<DualElement<Film, Genre>> rowList, Film film, List<Genre> genres) {
        List<Film> films = new ArrayList<>();
        for (DualElement<Film, Genre> filmGenre : rowList) {
            if (filmGenre.getFirst() == null) {
                throw new EntityNotFoundException("В базе нет фильмов");
            }
            if (!film.equals(filmGenre.getFirst())) {
                film = filmGenre.getFirst();
                films.add(film);
            }
            if (filmGenre.getSecond().getName() != null && film.getGenres().isEmpty()) {
                genres = new ArrayList<>();
                film.setGenres(genres);
            }
            if (filmGenre.getSecond() != null) {
                genres.add(filmGenre.getSecond());
            }
        }
        return films;
    }

    private static RowMapper<DualElement<Film, Genre>> getFilmTempMapper() {
        return (rs, rowNum) -> {
            Film film = new Film(
                    rs.getLong("film_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getLong("duration"),
                    new Rating(rs.getInt("rating_id"),
                            rs.getString("rating")),
                    rs.getInt("likes"));
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre"));
            return new DualElement<Film, Genre>(film, genre);
        };
    }
}
