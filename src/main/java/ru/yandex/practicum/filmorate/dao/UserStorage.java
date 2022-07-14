package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

public interface UserStorage {

    HashMap<Integer, User> getUsers();

    User getUser(int userId);

    User saveUser(User user);

    User updateUser(User user);

    void deleteUser(int userId);

}
