package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    Map<Long, User> users = new HashMap<>();
    private Long counter = 1L;

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        user.setId(counter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean isExist(Long id) {
        return users.containsKey(id);
    }

    @Override
    public List<User> getFriends(Long id) {
        Set<Long> friends = getUserById(id).getFriends();
        return friends.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }
}
