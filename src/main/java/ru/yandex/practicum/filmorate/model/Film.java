package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.customValidator.MinimumDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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
    private int rating;
    private Set<Genre> genres = new TreeSet<>((genre1, genre2) -> {
        return genre2.getId() - genre1.getId();
    });
    private int like = 0;
    Set<Long> likes = new HashSet<>();

    public Film(Long id, String name, String description, LocalDate releaseDate, long duration, int rating, int like) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rating = rating;
        this.like = like;
    }

    public void addLike(Long id) {
        likes.add(id);
    }

    public void deleteLike(Long id) {
        likes.remove(id);
    }
}
