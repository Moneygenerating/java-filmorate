package ru.yandex.practicum.filmorate.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

@Component
@Getter
@NoArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    protected int usersId = 0;

    protected HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User getUser(int userId) {
        return users.get(userId);
    }

    @Override
    public User saveUser(User user) {
        user.setId(++usersId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(int userId) {
        users.remove(userId);
    }
}
