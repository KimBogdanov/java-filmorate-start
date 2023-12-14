package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.List;

@Service
public class RatingService {
    RatingStorage ratingStorage;

    @Autowired
    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public List<Rating> getRatings() {
        return ratingStorage.getAll();
    }

    public Rating getById(Integer id) {
        if (isExist(id)) {
            throw new EntityNotFoundException("Рейтинга с id " + id + " нет в базе");
        }
        return ratingStorage.getById(id);
    }

    public boolean isExist(Integer id) {
        return ratingStorage.isExist(id);
    }
}
