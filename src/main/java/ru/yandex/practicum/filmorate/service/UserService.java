package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.dao.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserBirthdayException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Collection<User> findAll() {
        return inMemoryUserStorage.getUsers().values();
    }

    public User createUser(User user) {
        checkEmail(user);
        validateBirthdayAndName(user);

        if (inMemoryUserStorage.getUsers().containsKey(user.getId())) {
            throw new UserAlreadyExistException(String.format(
                    "Пользователь с таким id %s уже зарегистрирован.",
                    user.getId()
            ));
        }
        return inMemoryUserStorage.saveUser(user);
    }

    public User updateUser(User user) {
        checkEmail(user);
        validateBirthdayAndName(user);
        if (!inMemoryUserStorage.getUsers().containsKey(user.getId())) {
            throw new UserAlreadyExistException(String.format(
                    "Пользователь с id %s не найден.",
                    user.getId()
            ));
        }
        return inMemoryUserStorage.updateUser(user);
    }

    public User findUserById(Integer id) {
        if (id == null || inMemoryUserStorage.getUser(id) == null) {
            throw new UserNotFoundException("Пользователь с таким id не найден.");
        }
        return inMemoryUserStorage.getUser(id);
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

        if (inMemoryUserStorage.getUser(userId) == null || inMemoryUserStorage.getUser(friendId) == null) {
            throw new UserNotFoundException("Пользователи с такими id не найдены, добавление в друзья не получилось");
        }

        User user = inMemoryUserStorage.getUser(userId);
        User friend = inMemoryUserStorage.getUser(friendId);

        inMemoryUserStorage.addFriend(user, friend);
    }

    public void deleteFriend(int userId, int friendId) {

        if (inMemoryUserStorage.getUser(userId) == null || inMemoryUserStorage.getUser(friendId) == null) {
            throw new UserNotFoundException("Пользователи с такими id не найдены, удаление из друзей не получилось");
        }
        User user = inMemoryUserStorage.getUser(userId);
        User friend = inMemoryUserStorage.getUser(friendId);
        inMemoryUserStorage.deleteFriend(user, friend);
    }

    //возвращаем список пользователей, являющихся его друзьями
    public List<User> findUserFriendsById(Integer id) {
        if (id == null || inMemoryUserStorage.getUser(id) == null) {
            throw new UserNotFoundException("Пользователь с таким id не найден.");
        }
        return inMemoryUserStorage.getUser(id)
                .getFriendId()
                .stream()
                .map(inMemoryUserStorage::getUser)
                .collect(Collectors.toList());
    }

    // список друзей, общих с другим пользователем.
    public List<User> findSameUsersFriends(Integer id, Integer otherId) {
        HashSet<Integer> ne1 = inMemoryUserStorage.getUser(id).getFriendId();
        HashSet<Integer> ne2 = inMemoryUserStorage.getUser(otherId).getFriendId();

        //Оставим только похожие id
        ne1.retainAll(ne2);

        return ne1.stream()
                .map(inMemoryUserStorage::getUser)
                .collect(Collectors.toList());
    }
}