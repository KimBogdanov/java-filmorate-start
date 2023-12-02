package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        User user = users.get(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }
}
