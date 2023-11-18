package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.exception.InvalidLoginException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
public class UserController {
    private Long counter = 1L;
    Map<Long, User> users = new HashMap<>();

    @GetMapping("/users")
    public List<User> getUsers() {
        log.info("Get all users");
        return new ArrayList<>(users.values());
    }

    @PostMapping("/users")
    public User createUsers(@Valid @RequestBody User user) {
        log.info("Создаем юзера id= " + counter);
        if (validateLogin(user.getLogin())) {
            if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
                user.setName(user.getLogin());
                log.info("У юзера нет имени");
            }
            log.info("Юзер создан id = " + counter);
            user.setId(counter++);
            users.put(user.getId(), user);
        } else {
            log.info("Юзер id= " + counter + "не прошел валидацию логина");
            throw new InvalidLoginException("Логин не должен содержать пробелов");
        }
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new EntityNotExistException("UpdateUser, user exist");
        }
    }

    public static boolean validateLogin(String login) {
        String regx = "^[\\p{L} .'-]+$";
        Pattern pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(login);
        return matcher.find();
    }
}
