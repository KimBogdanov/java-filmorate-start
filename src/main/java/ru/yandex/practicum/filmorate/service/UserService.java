package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

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
        if (isExist(user.getId())) {
            throw new EntityNotFoundException("User с id " + user.getId() + " нет в базе");
        }
        return userStorage.updateUser(user);
    }

    public User getUser(Long id) {
        if (isExist(id)) {
            throw new EntityNotFoundException("User с id " + id + " нет в базе");
        }
        return getUserById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        if (isExist(userId)) {
            throw new EntityNotFoundException("User с id " + userId + " нет в базе");
        }
        if (isExist(friendId)) {
            throw new EntityNotFoundException("User с id " + friendId + " нет в базе");
        }
        friendsStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (isExist(userId)) {
            throw new EntityNotFoundException("User с id " + userId + " нет в базе");
        }
        if (isExist(friendId)) {
            throw new EntityNotFoundException("User с id " + friendId + " нет в базе");
        }
        friendsStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(Long id) {
        if (isExist(id)) {
            throw new EntityNotFoundException("User с id " + id + " нет в базе");
        }
        return friendsStorage.getFriends(id);
    }

    public List<User> getMutualFriends(Long userId, Long friendId) {
        if (isExist(userId)) {
            throw new EntityNotFoundException("User с id " + userId + " нет в базе");
        }
        if (isExist(friendId)) {
            throw new EntityNotFoundException("User с id " + friendId + " нет в базе");
        }
        return friendsStorage.getMutualFriends(userId, friendId);
    }

    public User getUserById(Long id) {
        if (isExist(id)) {
            throw new EntityNotFoundException("User с id " + id + " нет в базе");
        }
        return userStorage.getUserById(id);
    }

    public boolean isExist(Long userId) {
        return userStorage.isExist(userId);
    }
}
