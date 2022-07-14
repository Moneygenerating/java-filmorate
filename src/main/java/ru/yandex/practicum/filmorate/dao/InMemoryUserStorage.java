package ru.yandex.practicum.filmorate.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

@Component
@Getter
@NoArgsConstructor
public class InMemoryUserStorage implements UserStorage{

    protected int usersId = 0;

    protected HashMap<Integer, User> userHashMap = new HashMap<>();

    @Override
    public User getUser(int userId) {
        return userHashMap.get(userId);
    }

    @Override
    public User saveUser(User user) {
        user.setId(++usersId);
        userHashMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user){
        userHashMap.put(user.getId(),user);
        return userHashMap.get(user.getId());
    }

    @Override
    public void deleteUser(User user) {
        userHashMap.remove(user.getId());
    }
}
