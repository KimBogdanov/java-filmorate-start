package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class RatingController {
    RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping()
    public List<Rating> getRatings() {
        log.info("getRatings");
        return ratingService.getRatings();
    }

    @GetMapping("/{id}")
    public Rating getRating(@PathVariable Integer id) {
        log.info("getRating id {} ", id);
        return ratingService.getById(id);
    }
}
