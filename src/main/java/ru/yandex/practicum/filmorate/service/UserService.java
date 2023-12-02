package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
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
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        getUserIfExist(user.getId());
        return userStorage.updateUser(user);
    }

    public User getUser(Long id) {
        return getUserIfExist(id);
    }

    public User addFriend(Long userId, Long friendId) {
        User user = getUserIfExist(userId);
        User friend = getUserIfExist(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        userStorage.updateUser(friend);
        return userStorage.updateUser(user);
    }

    public User deleteFriend(Long userId, Long friendId) {
        User user = getUserIfExist(userId);
        User friend = getUserIfExist(friendId);
        user.deleteFriend(userId);
        friend.deleteFriend(friendId);
        userStorage.updateUser(friend);
        return userStorage.updateUser(user);
    }

    public List<User> getFriends(Long id) {
        User user = getUserIfExist(id);
        return getUsers().stream()
                .filter(u -> user.getFriends().contains(u.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Long userId, Long friendId) {
        User user = getUserIfExist(userId);
        User userFriend = getUserIfExist(friendId);
        List<Long> mutualId = user.getFriends().stream()
                .filter(id -> userFriend.getFriends().contains(id))
                .collect(Collectors.toList());
        return mutualId.stream()
                .map(this::getUserIfExist)
                .collect(Collectors.toList());
    }

    public User getUserIfExist(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new EntityNotFoundException("Не найден user id {} " + id);
        }
        return user;
    }
}
