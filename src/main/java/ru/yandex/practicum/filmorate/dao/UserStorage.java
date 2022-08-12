package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;
import java.util.HashMap;
import java.util.List;

public interface UserStorage {

    List<User> getUsers();

    User getUser(int userId);

    User saveUser(User user);

    User updateUser(User user);

    void deleteUser(int userId);

    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer friendId);

}
