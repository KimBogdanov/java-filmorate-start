package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {
    void addFriend(Long id, Long friend);

    void deleteFriend(Long id, Long friend);

    List<User> getMutualFriends(Long id, Long friend);

    List<User> getFriends(Long id);
}
