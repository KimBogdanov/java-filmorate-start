package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Long counter = 1L;
    Map<Long, User> users = new HashMap<>();

    @GetMapping()
    public List<User> getUsers() {
        log.info("Get all users");
        return new ArrayList<>(users.values());
    }

    @PostMapping()
    public User createUsers(@Valid @RequestBody User user) {
        log.info("Создаем юзера id= " + counter);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("У юзера нет имени");
        }
        log.info("Юзер создан id = " + counter);
        user.setId(counter++);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new EntityNotFoundException("UpdateUser, user not found");
        }
    }
}
