package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getGenres() {
        return genreStorage.getAll();
    }

    public Genre getGenre(Integer id) {
        if (isExist(id)) {
            throw new EntityNotFoundException("Жанра с id " + id + " нет в базе");
        }
        return genreStorage.getById(id);
    }

    public boolean isExist(Integer id) {
        return genreStorage.isExist(id);
    }
}
