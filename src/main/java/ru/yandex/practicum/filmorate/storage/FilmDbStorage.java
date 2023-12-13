package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.DualElement;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

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
        Film film = null;
        List<Genre> genres = new ArrayList<>();
        List<Film> films = new ArrayList<>();
        for (DualElement<Film, Genre> filmGenreDualElement : rowList) {
            if (film == null) {
                film = filmGenreDualElement.getFirst();
            }
            if(film == null){
                throw new EntityNotFoundException("В базе нет фильмов");
            }
            film.setGenres(genres);
            films.add(film);
            if (!film.equals(filmGenreDualElement.getFirst())){
                film = new Film();
                genres = new ArrayList<>();
            }
            genres.add(filmGenreDualElement.getSecond());
        }
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
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
        for (DualElement<Film, Genre> filmGenreDualElement : rowList) {
            if (film == null) {
                film = filmGenreDualElement.getFirst();
            }
            genres.add(filmGenreDualElement.getSecond());
        }
        if (film == null) {
            throw new EntityNotFoundException("Фильма нет в базе id=" + id);
        }
        film.setGenres(genres);
        return film;
    }

    @Override
    public boolean isExist(Long id) {
        return true;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return null;
    }

    private static RowMapper<DualElement<Film, Genre>> getFilmTempMapper() {
        return (rs, rowNum) -> {
            Film film = new Film(
                    rs.getLong("film_id"),
                    rs.getString("title"),
                    rs.getString("description"),
//                rs.getDate("release_date").toLocalDate(),
                    rs.getLong("duration"),
                    new Rating(rs.getInt("rating_id"),
                            rs.getString("rating")),
                    rs.getInt("likes"));
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre"));
            return new DualElement<Film, Genre>(film, genre);
        };
    }
}
