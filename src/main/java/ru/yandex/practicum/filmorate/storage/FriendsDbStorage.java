package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

@Repository
public class FriendsDbStorage implements FriendsStorage {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        isExistId(id);
        isExistId(friendId);
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friends")
                .usingColumns("person_id", "friends_id");
        insert.execute(Map.of("person_id", id, "friends_id", friendId));
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        String sql = "DELETE FROM FRIENDS WHERE PERSON_ID = ? AND FRIENDS_ID = ?";
        jdbcTemplate.update(sql, id, friendId);
    }

    private void isExistId(Long id) {
        String sql = "select * from PERSON where PERSON_ID = ?";
        if (!jdbcTemplate.queryForRowSet(sql, id).next()) {
            throw new EntityNotFoundException("Message");
        }
    }

    @Override
    public List<User> getFriends(Long id) {
        String sql = "SELECT * FROM PERSON " +
                "WHERE PERSON_ID IN (SELECT FRIENDS_ID FROM FRIENDS WHERE PERSON_ID = ?)";
        return jdbcTemplate.query(sql, getUserMapper(), id);
    }

    private static RowMapper<User> getUserMapper() {
        return (rs, rowNum) -> new User(
                rs.getLong("person_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }

    @Override
    public List<User> getMutualFriends(Long id, Long otherId) {
        String sql = "SELECT * FROM PERSON " +
                "WHERE PERSON_ID in (SELECT FRIENDS_ID FROM FRIENDS " +
                "WHERE PERSON_ID = ?  AND  FRIENDS_ID IN(SELECT FRIENDS_ID FROM FRIENDS WHERE PERSON_ID = ?))";
        return jdbcTemplate.query(sql, getUserMapper(), id, otherId);
    }
}
