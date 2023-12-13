package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
@RequiredArgsConstructor
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public List<Film> getFilms() {
        return jdbcTemplate.query("SELECT * FROM film AS F JOIN RATING R on F.RATING_ID = R.RATING_ID ", getFilmMapper());
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
        return null;
    }

    @Override
    public boolean isExist(Long id) {
        return true;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return null;
    }
    private static RowMapper<Film> getFilmMapper() {
        return (rs, rowNum) -> new Film(
                rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_data").toLocalDate(),
                rs.getLong("duration"),
                rs.getInt("rating_id"),
                rs.getInt("likes")
        );
    }
}
