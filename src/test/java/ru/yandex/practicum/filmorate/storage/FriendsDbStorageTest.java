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
class FriendsDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FriendsStorage friendsStorage;
    private UserStorage userStorage;

    @BeforeEach
    public void setUp() {
        friendsStorage = new FriendsDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    void addFriend() {
        User user = userStorage.createUser(new User("pop@pop.com",
                "login", "name", LocalDate.of(2008, Month.DECEMBER, 05)));
        User user2 = userStorage.createUser(new User("pop@mail.com",
                "login1", "name1", LocalDate.of(2010, Month.DECEMBER, 05)));
        friendsStorage.addFriend(user.getId(), user2.getId());
        List<User> friends = friendsStorage.getFriends(user.getId());
        assertEquals(List.of(user2), friends);
    }

    @Test
    void deleteFriend() {
        User user = userStorage.createUser(new User("pop@pop.com",
                "login", "name", LocalDate.of(2008, Month.DECEMBER, 05)));
        User user2 = userStorage.createUser(new User("pop@mail.com",
                "login1", "name1", LocalDate.of(2010, Month.DECEMBER, 05)));
        String sql = "SELECT * FROM FRIENDS";
        friendsStorage.addFriend(user.getId(), user2.getId());
        friendsStorage.deleteFriend(user.getId(), user2.getId());
        List<List<Integer>> resultList = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return List.of(rs.getInt("person_id"),
                    rs.getInt("friends_id"));
        });
        List<Integer> friends = resultList.isEmpty() ? List.of() : resultList.get(0);
        assertEquals(List.of(), friends);
    }

    @Test
    void getFriends() {
        User user = userStorage.createUser(new User("pop@pop.com",
                "login", "name", LocalDate.of(2008, Month.DECEMBER, 05)));
        User user2 = userStorage.createUser(new User("pop@mail.com",
                "login1", "name1", LocalDate.of(2010, Month.DECEMBER, 05)));
        friendsStorage.addFriend(user.getId(), user2.getId());
        List<User> friends = friendsStorage.getFriends(user.getId());
        assertEquals(List.of(user2), friends);
    }

    @Test
    void getMutualFriends() {
        User user = userStorage.createUser(new User("pop@pop.com",
                "login", "name", LocalDate.of(2008, Month.DECEMBER, 05)));
        User user2 = userStorage.createUser(new User("pop@mail.com",
                "login1", "name1", LocalDate.of(2010, Month.DECEMBER, 05)));
        User user3 = userStorage.createUser(new User("pop@popo.com",
                "login", "name", LocalDate.of(2008, Month.DECEMBER, 05)));
        User user4 = userStorage.createUser(new User("popy@mail.com",
                "login1", "name1", LocalDate.of(2010, Month.DECEMBER, 05)));
        friendsStorage.addFriend(user.getId(), user3.getId());
        friendsStorage.addFriend(user.getId(), user4.getId());
        friendsStorage.addFriend(user2.getId(), user3.getId());
        friendsStorage.addFriend(user2.getId(), user4.getId());
        List<User> friends = friendsStorage.getMutualFriends(user.getId(), user2.getId());
        assertEquals(List.of(user3, user4), friends);
    }
}