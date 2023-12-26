package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@Repository
public class RatingDbStorage implements RatingStorage {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Rating> getAll() {
        String sql = "SELECT * FROM rating";
        return jdbcTemplate.query(sql, getRatingMapper());
    }

    @Override
    public Rating getById(Integer id) {
        String sql = "SELECT * FROM rating WHERE rating_id = ?";
        return jdbcTemplate.queryForObject(sql, getRatingMapper(), id);
    }

    @Override
    public boolean isExist(Integer id) {
        String sql = "SELECT * FROM RATING WHERE RATING_ID = ?";
        return !jdbcTemplate.queryForRowSet(sql, id).next();
    }

    private static RowMapper<Rating> getRatingMapper() {
        return ((rs, rowNum) -> new Rating(rs.getInt("rating_id"),
                rs.getString("rating")));
    }
}
