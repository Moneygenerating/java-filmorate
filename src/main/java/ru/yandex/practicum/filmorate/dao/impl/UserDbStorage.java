package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        final String sqlQuery = "SELECT USER_ID,LOGIN,USER_NAME,EMAIL,BIRTHDAY FROM USERS ";
        final List<User> users = jdbcTemplate.query(sqlQuery, UserDbStorage::makeUser);
        return users;
    }

    @Override
    public User getUser(int userId) {
        final String sqlQuery = "SELECT USER_ID, LOGIN,USER_NAME, EMAIL, BIRTHDAY FROM USERS WHERE USER_ID = ?";
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

        //как count сделать
        final String sqlSingle = "SELECT COUNT(USER_ID) FROM USERS WHERE LOGIN = ?";
        Integer countLogins =  jdbcTemplate.queryForObject(sqlSingle, Integer.class);
         */
        return users.get(0);
    }

    //Получить id друзей, которые добавили друг друга
    public List<User> getCommonFriendsByUserId(int id, int friendId) {
        final String sqlQuery = "SELECT u.USER_ID,u.LOGIN,u.USER_NAME,u.EMAIL,u.BIRTHDAY FROM USERS AS u LEFT JOIN FRIENDS AS f " +
                "ON u.USER_ID=f.USER_ID WHERE f.USER_ID = ? AND f.FRIENDS_ID = ?";
        final List<User> commonFriendlyUsers = jdbcTemplate.query(sqlQuery, UserDbStorage::makeUser, friendId, id);

        if (commonFriendlyUsers.size() == 0) {
            return null;
        }
        return commonFriendlyUsers;
    }

    static User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User((rs.getInt("USER_ID")),
                rs.getString("LOGIN"),
                rs.getString("USER_NAME"),
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

    //Получить друзей пользователя
    @Override
    public Set<User> getUserFriendsById(int id){
        String sqlQuery = "SELECT * FROM USERS AS u LEFT JOIN FRIENDS AS f ON u.USER_ID = f.USER_ID WHERE f.FRIENDS_ID = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, UserDbStorage::makeUser,id);
        LinkedHashSet<User> us = new LinkedHashSet<>(users);
        return us;
    }
    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE USERS SET USER_NAME = ?,LOGIN = ?,EMAIL =?, BIRTHDAY =? WHERE FILM_ID=?";

        jdbcTemplate.update(sqlQuery
                , user.getName()
                , user.getLogin()
                , user.getEmail()
                , user.getBirthday()
                , user.getId());

        return user;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        String sqlQuery = "SELECT USER_ID, FILM_ID FROM FILM_LIKES WHERE USER_ID= ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void deleteFriend(Integer friendId) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE FRIEND_ID= ?";
        jdbcTemplate.update(sqlQuery, friendId);
    }

    @Override
    public void deleteUser(int userId) {
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID= ?";
        jdbcTemplate.update(sqlQuery, userId);
    }
}
