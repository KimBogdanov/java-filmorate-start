package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.customValidator.MinimumDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200, message = "Описание не может быть длинне 200 символов")
    private String description;
    @MinimumDate(minDate = "1895-12-28")
    private LocalDate releaseDate;
    @Positive
    private long duration;
    private Rating rating;
    private List<Genre> genres = new ArrayList<>();
    private int like = 0;

    public Film() {
    }

    public Film(Long id, String name, String description, long duration, Rating rating, int like) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.rating = rating;
        this.like = like;
    }
}
