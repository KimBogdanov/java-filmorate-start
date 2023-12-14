package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmStorage storage;
    private UserStorage userStorage;

    @BeforeEach
    public void setUp() {
        storage = new FilmDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    void getFilmsIfFilmsNotExist() {
        List<Film> films = storage.getFilms();
        assertEquals(List.of(), films);
    }

    @Test
    void getFilmsOneFilm() {
        Film film = storage.createFilm(new Film("name", "desc",
                LocalDate.of(2000, Month.AUGUST, 25),
                120L, new Rating(1, "G"),
                List.of(new Genre(1, "Комедия")), 8));
        List<Film> films = storage.getFilms();
        assertEquals(List.of(film), films);
    }

    @Test
    void getFilmsFiveFilms() {
        Film film1 = storage.createFilm(new Film("name", "desc",
                LocalDate.of(2000, Month.AUGUST, 25),
                120L, new Rating(1, "G"),
                List.of(new Genre(1, "Комедия")), 8));
        Film film2 = storage.createFilm(new Film("name", "desc",
                LocalDate.of(2000, Month.AUGUST, 25),
                120L, new Rating(1, "G"),
                List.of(new Genre(1, "Комедия")), 8));
        Film film3 = storage.createFilm(new Film("name", "desc",
                LocalDate.of(2000, Month.AUGUST, 25),
                120L, new Rating(1, "G"),
                List.of(new Genre(1, "Комедия")), 8));
        Film film4 = storage.createFilm(new Film("name", "desc",
                LocalDate.of(2000, Month.AUGUST, 25),
                120L, new Rating(1, "G"),
                List.of(new Genre(1, "Комедия")), 8));
        Film film5 = storage.createFilm(new Film("name", "desc",
                LocalDate.of(2000, Month.AUGUST, 25),
                120L, new Rating(1, "G"),
                List.of(new Genre(1, "Комедия")), 8));
        List<Film> films = storage.getFilms();
        assertEquals(List.of(film1, film2, film3, film4, film5), films);
    }

    @Test
    void createFilm() {
        Film film = storage.createFilm(new Film("name", "desc",
                LocalDate.of(2000, Month.AUGUST, 25),
                120L, new Rating(1, "G"),
                List.of(new Genre(1, "Комедия")), 8));
        Film filmById = storage.getFilmById(film.getId());
        assertEquals(film, filmById);
    }

    @Test
    void updateFilm() {
        Film film = storage.createFilm(new Film("name", "desc",
                LocalDate.of(2000, Month.AUGUST, 25),
                120L, new Rating(1, "G"),
                List.of(new Genre(1, "Комедия")), 8));
        Film film2 = storage.createFilm(new Film("changeName", "changeDesc",
                LocalDate.of(2200, Month.AUGUST, 25),
                50L, new Rating(1, "G"),
                List.of(new Genre(1, "Комедия")), 12));
        film2.setId(film.getId());
        storage.updateFilm(film2);
        Film filmById = storage.getFilmById(film.getId());
        assertEquals(film2, filmById);
    }

    @Test
    void addLike() {
        Film film = storage.createFilm(new Film("name", "desc",
                LocalDate.of(2000, Month.AUGUST, 25),
                120L, new Rating(1, "G"),
                List.of(new Genre(1, "Комедия")), 0));
        User user = userStorage.createUser(new User("pop@pop.com",
                "login", "name", LocalDate.of(2008, Month.DECEMBER, 05)));
        storage.addLike(film.getId(), user.getId());
        int likes = storage.getFilmById(film.getId()).getLikes();
        String sql = "SELECT * FROM FILM_LiKE";
        List<Integer> integers = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            return List.of(rs.getInt("person_id"),
                    rs.getInt("film_id"));
        });
        assertEquals(1, likes);
        assertEquals(List.of(1, 1), integers);
    }

    @Test
    void deleteLike() {
        Film film = storage.createFilm(new Film("name", "desc",
                LocalDate.of(2000, Month.AUGUST, 25),
                120L, new Rating(1, "G"),
                List.of(new Genre(1, "Комедия")), 0));
        User user = userStorage.createUser(new User("pop@pop.com",
                "login", "name", LocalDate.of(2008, Month.DECEMBER, 05)));
        storage.addLike(film.getId(), user.getId());
        storage.deleteLike(film.getId(), user.getId());
        int likes = storage.getFilmById(film.getId()).getLikes();
        String sql = "SELECT * FROM FILM_LiKE";
        List<List<Integer>> resultList = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return List.of(rs.getInt("person_id"),
                    rs.getInt("film_id"));
        });
        List<Integer> integers = resultList.isEmpty() ? List.of() : resultList.get(0);
        assertEquals(0, likes);
        assertEquals(List.of(), integers);
    }
}