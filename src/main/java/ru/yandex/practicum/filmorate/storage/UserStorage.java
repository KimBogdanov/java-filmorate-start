package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    User createUser(User user);

    User getUserById(Long id);

    User updateUser(User user);
}
