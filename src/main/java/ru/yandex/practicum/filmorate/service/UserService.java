package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    UserStorage userStorage;
    FriendsStorage friendsStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendsStorage friendsStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUser(Long id) {
        return getUserById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        friendsStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        friendsStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(Long id) {
        return friendsStorage.getFriends(id);
    }

    public List<User> getMutualFriends(Long userId, Long friendId) {
        return friendsStorage.getMutualFriends(userId, friendId);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public boolean isExist(Long userId) {
        return userStorage.isExist(userId);
    }
}
