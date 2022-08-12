package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.LikeStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dao.impl.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserBirthdayException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    private final UserStorage userDbStorage;
    private final FilmStorage filmDbStorage;
    private final LikeStorage likeDbStorage;


    @Autowired
    public UserService(UserStorage userDbStorage, FilmStorage filmDbStorage, LikeStorage likeDbStorage) {
        this.userDbStorage = userDbStorage;
        this.filmDbStorage = filmDbStorage;
        this.likeDbStorage = likeDbStorage;
    }

    public Collection<User> findAll() {
        return userDbStorage.getUsers().values();
    }

    public User createUser(User user) {
        checkEmail(user);
        validateBirthdayAndName(user);

        if (userDbStorage.getUsers().containsKey(user.getId())) {
            throw new UserAlreadyExistException(String.format(
                    "Пользователь с таким id %s уже зарегистрирован.",
                    user.getId()
            ));
        }
        return userDbStorage.saveUser(user);
    }

    public User updateUser(User user) {
        checkEmail(user);
        validateBirthdayAndName(user);
        if (!userDbStorage.getUsers().containsKey(user.getId())) {
            throw new UserAlreadyExistException(String.format(
                    "Пользователь с id %s не найден.",
                    user.getId()
            ));
        }
        return userDbStorage.updateUser(user);
    }

    public User findUserById(Integer id) {
        if (id == null || userDbStorage.getUser(id) == null) {
            throw new UserNotFoundException("Пользователь с таким id не найден.");
        }
        return userDbStorage.getUser(id);
    }

    void checkEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidEmailException("Адрес электронной почты не может быть пустым.");
        }
    }

    void validateBirthdayAndName(User user) {
        //проверка даты
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new UserBirthdayException("Некорректный день рождения");
        }
        //проверка имени
        if (user.getName() == "") {
            user.setName(user.getLogin());
        }
    }

    public void addFriend(int userId, int friendId) {

        if (userDbStorage.getUser(userId) == null || userDbStorage.getUser(friendId) == null) {
            throw new UserNotFoundException("Пользователи с такими id не найдены, добавление в друзья не получилось");
        }

        User user = userDbStorage.getUser(userId);
        User friend = userDbStorage.getUser(friendId);

        userDbStorage.addFriend(user, friend);
    }

    public void deleteFriend(int userId, int friendId) {

        if (userDbStorage.getUser(userId) == null || userDbStorage.getUser(friendId) == null) {
            throw new UserNotFoundException("Пользователи с такими id не найдены, удаление из друзей не получилось");
        }
        User user = userDbStorage.getUser(userId);
        User friend = userDbStorage.getUser(friendId);
        userDbStorage.deleteFriend(user, friend);
    }

    //возвращаем список пользователей, являющихся его друзьями
    public Stream<User> findUserFriendsById(Integer id) {
        if (id == null || userDbStorage.getUser(id) == null) {
            throw new UserNotFoundException("Пользователь с таким id не найден.");
        }

        return userDbStorage.getUser(id).getFriendId()
                .stream()
                .map(userDbStorage::getUser);
    }

    // список друзей, общих с другим пользователем.
    public Stream<User> findSameUsersFriends(Integer id, Integer otherId) {
        if (id == null || userDbStorage.getUser(id) == null) {
            throw new UserNotFoundException("Пользователь с таким id не найден.");
        }

        if (otherId == null || userDbStorage.getUser(otherId) == null) {
            throw new UserNotFoundException("Пользователь с таким вторым id не найден.");
        }

        return userDbStorage.getUsers().values().stream()
                .filter(u -> id.equals(u.getId()) || otherId.equals(u.getId()))
                .map(User::getFriendId)
                .flatMap(Collection::stream)
                // Creates a map type -> {4:1, 5:2, 7:2, 8:2, 9:1}
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                // Convert back to stream to filter
                .stream()
                //get values(only doubles, same >1)
                .filter(element -> element.getValue() > 1)
                //execute values
                .map(Map.Entry::getKey)
                .map(userDbStorage::getUser);
    }

}