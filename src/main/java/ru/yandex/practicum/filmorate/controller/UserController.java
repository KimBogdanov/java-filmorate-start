package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;
    FriendsStorage friendsStorage;

    public UserController(UserService userService, FriendsStorage friendsStorage) {
        this.userService = userService;
        this.friendsStorage = friendsStorage;
    }

    @GetMapping()
    public List<User> getUsers() {
        log.info("getUsers");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        log.info("getUser " + id);
        return userService.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("getFriends for user " + id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable Long id,
                                       @PathVariable Long otherId) {
        log.info("getMutualFriends id " + id + "id " + otherId);
        return userService.getMutualFriends(id, otherId);
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        User newUser = userService.createUser(user);
        log.info("createUser " + newUser.getId());
        return newUser;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        log.info("updateUser " + user.getId());
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id,
                          @PathVariable Long friendId) {
        log.info("addFriend " + id + "id " + friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id,
                             @PathVariable Long friendId) {
        log.info("deleteFriend " + id + "id " + friendId);
        userService.deleteFriend(id, friendId);
    }
}