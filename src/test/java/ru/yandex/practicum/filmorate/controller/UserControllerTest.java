package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController;

    @BeforeEach
    public void setUp() {
        this.userController = new UserController();
    }

    @Test
    void shouldReturnEmptyList() {
        List<User> users = userController.getUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void shouldCreateUsers() {
        User user = userController.createUsers(new User("mail@mail.ru", "Login", "Name",
                LocalDate.of(1989, 10, 5)));
        assertEquals(user, userController.users.get(user.getId()));
    }

    @Test
    void shouldCreateTenUsers() {
        for (int i = 0; i < 10; i++) {
            userController.createUsers(new User("mail@mail.ru", "Login", "Name",
                    LocalDate.of(1989, 10, 5)));
        }
        assertEquals(10, userController.users.size());
    }

    @Test
    void shouldAddNameIfExist() {
        User user = userController.createUsers(new User("mail@mail.ru", "Login", " ",
                LocalDate.of(1989, 10, 5)));
        assertEquals(user.getLogin(), user.getName());

        User user1 = userController.createUsers(new User("mail@mail.ru", "Login", null,
                LocalDate.of(1989, 10, 5)));
        assertEquals(user1.getLogin(), user1.getName());
    }

    @Test
    void shouldThrowExceptionIfUpdateUserExist() {
        User user = new User("mail@mail.ru", "Login", " ",
                LocalDate.of(1989, 10, 5));
        user.setId(15L);
        assertThrows(EntityNotFoundException.class, () -> userController.updateUser(user));
    }
}