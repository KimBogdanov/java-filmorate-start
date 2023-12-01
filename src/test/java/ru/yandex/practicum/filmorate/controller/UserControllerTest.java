package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {
    @Autowired
    UserController userController;

    @Test
    void shouldReturnEmptyList() {
        List<User> users = userController.getUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    void shouldCreateUsers() {
        User user = userController.createUser(new User("mail@mail.ru", "Login", "Name",
                LocalDate.of(1989, 10, 5)));
        assertTrue(userController.getUsers().contains(user));
    }

    @Test
    void shouldCreateTenUsers() {
        for (int i = 0; i < 10; i++) {
            userController.createUser(new User("mail@mail.ru", "Login", "Name",
                    LocalDate.of(1989, 10, 5)));
        }
        assertEquals(10, userController.getUsers().size());
    }

    @Test
    void shouldAddNameIfExist() {
        User user = userController.createUser(new User("mail@mail.ru", "Login", " ",
                LocalDate.of(1989, 10, 5)));
        assertEquals(user.getLogin(), user.getName());
        User user1 = userController.createUser(new User("mail@mail.ru", "Login", null,
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