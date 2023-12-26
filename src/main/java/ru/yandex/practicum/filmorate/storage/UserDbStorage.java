package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM person", getUserMapper());
    }

    @Override
    public User getUserById(Long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM person WHERE person_id = ?", getUserMapper(), id);
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
    public User createUser(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("person")
                .usingGeneratedKeyColumns("person_id");
        Long id = insert.executeAndReturnKey(userToMap(user)).longValue();
        user.setId(id);
        return user;
    }

    private Map<String, Object> userToMap(User user) {
        return Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday()
        );
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE person SET " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "where person_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return getUserById(user.getId());
    }

    @Override
    public boolean isExist(Long id) {
        String sql = "SELECT * FROM PERSON WHERE PERSON_ID = ?";
        return !jdbcTemplate.queryForRowSet(sql, id).next();
    }
}
