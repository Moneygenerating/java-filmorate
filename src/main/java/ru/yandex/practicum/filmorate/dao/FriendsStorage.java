package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

public interface FriendsStorage {

    void setFriends(User user);

    void loadFriends(User user);
}
