package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.customValidator.MinimumDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Data
public class Film {
    Long id;
    @NotBlank
    String name;
    @Size(max = 200, message = "Описание не может быть длинне 200 символов")
    String description;
    @MinimumDate(minDate = "1895-12-28")
    LocalDate releaseDate;
    @Positive
    long duration;
    Set<User> likes = new HashSet<>();
}
