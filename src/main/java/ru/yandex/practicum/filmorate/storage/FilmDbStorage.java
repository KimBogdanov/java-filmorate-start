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
        String sqlGenres = "SELECT fg.FILM_ID, g.* FROM FILM_GENRE AS fg LEFT JOIN GENRE AS g ON fg.GENRE_ID = g.GENRE_ID";
        List<Film> films = jdbcTemplate.query(sqlFilms, getFilmRatingMapper());
        List<Pair<Long, Genre>> genres = jdbcTemplate.query(sqlGenres, getFilmGenreMapper());
        return getFilms(films, genres);
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
                "TITLE = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ? , RATING_ID = ? " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        String sqlGenre = "SELECT fg.FILM_ID, g.* " +
                "FROM FILM_GENRE AS fg " +
                "LEFT JOIN GENRE AS g ON fg.GENRE_ID = g.GENRE_ID WHERE fg.FILM_ID = ?";
        List<Pair<Long, Genre>> idFilmGenres = jdbcTemplate.query(sqlGenre, getFilmGenreMapper(), film.getId());
        List<Genre> genres = extractGenres(idFilmGenres);
        List<Genre> genresUpdateFilm = film.getGenres();
        if (!genres.containsAll(genresUpdateFilm) || !genresUpdateFilm.containsAll(genres)) {
            String sqlDelete = "DELETE FROM film_genre WHERE FILM_ID = ?";
            jdbcTemplate.update(sqlDelete, film.getId());
            if (!film.getGenres().isEmpty()) {
                saveGenresToFilm(film.getId(), genresUpdateFilm);
            }
        }
        return film;
    }

    private List<Genre> extractGenres(List<Pair<Long, Genre>> idFilmGenres) {
        return idFilmGenres.stream()
                .map(Pair::getRight)
                .collect(Collectors.toList());
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
        List<Pair<Long, Genre>> idFilmGenres = jdbcTemplate.query(sqlGenre, getFilmGenreMapper(), id);
        film.getGenres().addAll(extractGenres(idFilmGenres));
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
        String sqlGenres = "SELECT fg.FILM_ID, g.* " +
                "FROM FILM_GENRE AS fg " +
                "LEFT JOIN GENRE AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE fg.FILM_ID IN (SELECT f.FILM_ID " +
                "FROM FILM AS f Left JOIN FILM_LIKE FL on f.FILM_ID = FL.FILM_ID " +
                "GROUP BY f.FILM_ID ORDER BY COUNT(fl.PERSON_ID) DESC  LIMIT ?)";
        List<Film> films = jdbcTemplate.query(sqlFilms, getFilmRatingMapper(), count);
        List<Pair<Long, Genre>> genres = jdbcTemplate.query(sqlGenres, getFilmGenreMapper(), count);
        return getFilms(films, genres);
    }

    private List<Film> getFilms(List<Film> films, List<Pair<Long, Genre>> genres) {
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
