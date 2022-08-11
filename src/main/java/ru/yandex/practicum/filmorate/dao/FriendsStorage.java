package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {

    void setFriends(User user);

    void loadFriends(User user);

    List<Integer> getFriendsByUserId(int id);
}
