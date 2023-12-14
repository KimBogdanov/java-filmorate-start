package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@JdbcTest
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;

    @BeforeEach
    public void setUp() {
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    void getUsersUsersNotExist() {
        List<User> users = userStorage.getUsers();
        assertEquals(List.of(), users);
    }

    @Test
    void getUserById() {
        User user = userStorage.createUser(new User("pop@pop.com",
                "login", "name", LocalDate.of(2008, Month.DECEMBER, 05)));
        User userById = userStorage.getUserById(user.getId());
        assertEquals(user, userById);
    }

    @Test
    void updateUser() {
        User user = userStorage.createUser(new User("pop@pop.com",
                "login", "name", LocalDate.of(2008, Month.DECEMBER, 05)));
        User user2 = userStorage.createUser(new User("pop@mail.com",
                "login1", "name1", LocalDate.of(2010, Month.DECEMBER, 05)));
        user2.setId(user.getId());
        userStorage.updateUser(user2);
        User userById = userStorage.getUserById(user.getId());
        assertEquals(user2, userById);
    }
}