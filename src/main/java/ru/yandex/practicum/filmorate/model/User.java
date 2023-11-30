package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    Long id;
    @NotBlank
    @Email
    String email;
    @NotBlank
    @Pattern(regexp = "^\\S+$")
    String login;
    String name;
    @Past
    LocalDate birthday;
    Set<User> friends = new HashSet<>();

    public User(String email, String login, String name, LocalDate birthday) { //for tests
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
