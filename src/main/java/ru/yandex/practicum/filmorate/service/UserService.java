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
        getUserByIdCheck(user.getId());
        return userStorage.updateUser(user);
    }

    public User getUser(Long id) {
        return getUserByIdCheck(id);
    }

    public User addFriend(Long userId, Long friendId) {
        User user = getUserByIdCheck(userId);
        User friendUser = getUserByIdCheck(friendId);
        user.addFriend(friendId);
        friendUser.addFriend(userId);
        return userStorage.getUserById(userId);
    }

    public User deleteFriend(Long userId, Long friendId) {
        User user = getUserByIdCheck(userId);
        User friend = getUserByIdCheck(friendId);
        user.deleteFriend(userId);
        friend.deleteFriend(friendId);
        return userStorage.getUserById(userId);
    }

    public List<User> getFriends(Long id) {
        User user = getUserByIdCheck(id);
        return getUsers().stream()
                .filter(u -> user.getFriends().contains(u.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Long userId, Long friendId) {
        User user = getUserByIdCheck(userId);
        User userFriend = getUserByIdCheck(friendId);
        List<Long> mutualId = user.getFriends().stream()
                .filter(id -> userFriend.getFriends().contains(id))
                .collect(Collectors.toList());
        return mutualId.stream()
                .map(this::getUserByIdCheck)
                .collect(Collectors.toList());
    }

    public User getUserByIdCheck(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new EntityNotFoundException("Не найден user id {} " + id);
        }
        return user;
    }
}
