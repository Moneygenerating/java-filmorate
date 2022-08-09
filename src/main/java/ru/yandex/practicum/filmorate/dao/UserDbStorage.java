package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public HashMap<Integer, User> getUsers() {
        return null;
    }

    @Override
    public User getUser(int userId) {
        final String sqlQuery = "SELECT USER_ID, USER_NAME, LOGIN, EMAIL, BIRTHDAY FROM USERS WHERE USER_ID = ?";
        final List<User> users = jdbcTemplate.query(sqlQuery, UserDbStorage::makeUser, userId);
        if (users.size() != 1) {
            return null;
        }

        /*
        final List<Map<String, Object>> maps = jdbcTemplate.queryForList(sqlQuery);
        final Object value = maps.get(0).values().iterator().next();
        Integer value2 = jdbcTemplate.queryForObject(sqlQuery, Integer.class);

        jdbcTemplate.queryForList(sqlQuery);
        rs.getInteger("USER_ID");

         */
        return users.get(0);

    }

    static User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User((rs.getInt("USER_ID")),
                rs.getString("LOGIN"),
                rs.getString("NAME"),
                rs.getString("EMAIL"),
                rs.getDate("BIRTHDAY").toLocalDate());

    }

    @Override
    public User saveUser(User user) {
        String sqlQuery = "INSERT INTO USERS (EMAIL,LOGIN,USER_NAME,BIRTHDAY) VALUES (?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            final LocalDate birthday = user.getBirthday();
            if (birthday == null) {
                stmt.setNull(4, Types.DATE);
            } else {
                stmt.setDate(4, Date.valueOf(birthday));
            }
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public void addFriend(User user, User friend) {
    }

    @Override
    public void deleteFriend(User user, User friend) {

    }

    @Override
    public void deleteUser(int userId) {

    }
}
