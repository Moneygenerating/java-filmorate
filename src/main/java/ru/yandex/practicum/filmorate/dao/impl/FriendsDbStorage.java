package ru.yandex.practicum.filmorate.dao.impl;

import com.sun.jdi.IntegerValue;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FriendsStorage;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Получить id друзей юзера
    @Override
    public List<Integer> getFriendsByUserId(int id) {
        final String sqlQuery = "SELECT FRIENDS_ID FROM FRIENDS WHERE USER_ID = ?";
        final List<Friend> friends = jdbcTemplate.query(sqlQuery, FriendsDbStorage::makeFriend, id);

        if (friends.size() == 0) {
            return null;
        }
        return friends.stream()
                .map(Friend::getFriendId).collect(Collectors.toList());
    }

    @Override
    public void setFriends(User user) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE USER_ID =?";

        if (user.getFriend() == null || user.getFriend().isEmpty()) {
            return;
        } else {
            jdbcTemplate.update(sqlQuery, user.getId());
        }

        for (Friend friend : user.getFriend()) {
            String sqlQueryGenre = "INSERT INTO FRIENDS (USER_ID, FRIENDS_ID) VALUES (?,?)";
            jdbcTemplate.update(sqlQueryGenre, user.getId(), friend.getFriendId());
        }

    }

    @Override
    public void loadFriends(User user) {
        String sqlQuery = "SELECT USER_ID,FRIENDS_ID FROM FRIENDS WHERE UER_ID = ?";
        Set<Friend> friends =(Set<Friend>) jdbcTemplate.query(sqlQuery,FriendsDbStorage::makeFriend,user.getId());

        user.setFriend(friends);
    }

    static Friend makeFriend(ResultSet rs, int rowNum) throws SQLException {
        return new Friend(rs.getInt("FRIENDS_ID"));

    }

}
