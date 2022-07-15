package ru.yandex.practicum.filmorate.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.HashSet;

@Component
@NoArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    protected int usersId = 0;
    protected HashMap<Integer, User> users = new HashMap<>();

    @Override
    public HashMap<Integer, User> getUsers(){
        return users;
    }

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

    @Override
    public void addFriend(User user, User friend){
        //для упрощения в друзья добавляются одновременно
        /*
        HashSet<Integer> userFriends = user.getFriendId();
        userFriends.add(friend.getId());
        user.setFriendId(userFriends);

        HashSet<Integer> friendFriends =friend.getFriendId();
        friendFriends.add(user.getId());
        friend.setFriendId(friendFriends);

         */

        user.getFriendId().add(friend.getId());
        friend.getFriendId().add(user.getId());
    }

    @Override
    public void deleteFriend(User user, User friend){
        //для упрощения из друзей удаляются одновременно
        user.getFriendId().remove(friend.getId());
        friend.getFriendId().remove(user.getId());
    }
}
