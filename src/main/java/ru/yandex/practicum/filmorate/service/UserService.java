package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private Long counter = 1L;
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(counter++);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        getUserById(user.getId());
        return userStorage.updateUser(user);
    }

    public User getUser(Long id) {
        return getUserById(id);
    }

    public void addFriend(Long id, Long friendId) {
        User userById = getUserById(id);
        User friendById = getUserById(friendId);
        userById.getFriends().add(friendById);
        friendById.getFriends().add(userById);
    }

    public void deleteFriend(Long id, Long deleteFriendId) {
        User userById = getUserById(id);
        User deleteFriend = getUserById(deleteFriendId);
        userById.getFriends().remove(deleteFriend);
        deleteFriend.getFriends().remove(userById);
    }

    public List<User> getFriends(Long id) {
        return new ArrayList<>(getUserById(id).getFriends());
    }

    public List<User> getMutualFriends(Long id, Long otherId) {
        return getUserById(id).getFriends().stream()
                .filter(user -> getUserById(otherId).getFriends().contains(user))
                .collect(Collectors.toList());
    }
    public User getUserById(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new EntityNotFoundException("Не найден user id {} " + id);
        }
        return user;
    }
}
