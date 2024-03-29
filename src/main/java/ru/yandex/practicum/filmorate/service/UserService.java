package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.FriendsStorage;
import ru.yandex.practicum.filmorate.dao.LikeStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserBirthdayException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    private final UserStorage userDbStorage;
    private final LikeStorage likeDbStorage;

    private final FriendsStorage friendsDbStorage;


    @Autowired
    public UserService(UserStorage userDbStorage, LikeStorage likeDbStorage,
                       FriendsStorage friendsDbStorage) {
        this.userDbStorage = userDbStorage;
        this.likeDbStorage = likeDbStorage;
        this.friendsDbStorage = friendsDbStorage;
    }

    public Collection<User> findAll() {
        List<User> users = userDbStorage.getUsers();
        //загружаем друзей и лайки
        friendsDbStorage.loadFriends(users);
        likeDbStorage.loadFilmLikesByUser(users);
        return users;
    }

    public User createUser(User user) {
        checkEmail(user);
        validateBirthdayAndName(user);
        List<Integer> usersID = userDbStorage.getUsers().stream().map(User::getId).collect(Collectors.toList());
        if (usersID.contains(user.getId())) {
            throw new UserAlreadyExistException(String.format(
                    "Пользователь с таким id %s уже зарегистрирован.",
                    user.getId()
            ));
        }

        final User newUser = userDbStorage.saveUser(user);

        //заполним друзьями и лайками дб
        if (user.getFriend() != null) {
            friendsDbStorage.setFriends(user);
        }
        if (user.getUserLikes() != null) {
            likeDbStorage.setFilmLikesByUser(user);
        }
        //загрузим друзей и лайки для нового пользователя
        friendsDbStorage.loadFriends(newUser);
        likeDbStorage.loadFilmLikesByUser(newUser);

        return newUser;
    }

    public User updateUser(User user) {

        checkEmail(user);
        validateBirthdayAndName(user);
        List<Integer> usersID = userDbStorage.getUsers().stream().map(User::getId).collect(Collectors.toList());
        if (!usersID.contains(user.getId())) {
            throw new UserAlreadyExistException(String.format(
                    "Пользователь с id %s не найден.",
                    user.getId()
            ));
        }
        //удаляем по юзеру связи лайков и друзей
        friendsDbStorage.deleteUserFriends(user);
        likeDbStorage.deleteFilmLikesByUser(user);

        final User newUser = userDbStorage.updateUser(user);
        //обновляем связи лайков и друзей
        if (user.getUserLikes() != null) {
            likeDbStorage.setFilmLikesByUser(newUser);
        }
        if (user.getFriend() != null) {
            friendsDbStorage.setFriends(newUser);
        }
        return newUser;
    }

    public User findUserById(Integer id) {

        if (id == null || userDbStorage.getUser(id) == null) {
            throw new UserNotFoundException("Пользователь с таким id не найден.");
        }

        final User user = userDbStorage.getUser(id);
        //загрузка друзей и лайков
        likeDbStorage.loadFilmLikesByUser(Collections.singletonList(user));
        friendsDbStorage.loadFriends(Collections.singletonList(user));

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

        userDbStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {

        if (userDbStorage.getUser(userId) == null || userDbStorage.getUser(friendId) == null) {
            throw new UserNotFoundException("Пользователи с такими id не найдены, удаление из друзей не получилось");
        }

        userDbStorage.deleteFriend(friendId);
    }

    //возвращаем список пользователей, являющихся его друзьями
    public Stream<User> findUserFriendsById(Integer id) {
        if (id == null || userDbStorage.getUser(id) == null) {
            throw new UserNotFoundException("Пользователь с таким id не найден.");
        }

        User user = userDbStorage.getUser(id);
        friendsDbStorage.loadFriends(user);
        return user.getFriend().stream().mapToInt(Friend::getFriendId)
                .mapToObj(userDbStorage::getUser);
    }

    // список друзей, общих с другим пользователем.
    public Stream<User> findSameUsersFriends(Integer id, Integer otherId) {
        if (id == null || userDbStorage.getUser(id) == null) {
            throw new UserNotFoundException("Пользователь с таким id не найден.");
        }

        if (otherId == null || userDbStorage.getUser(otherId) == null) {
            throw new UserNotFoundException("Пользователь с таким вторым id не найден.");
        }

        List<User> users = userDbStorage.getUsers();
        friendsDbStorage.loadFriends(users);

        Stream<User> userStreamFilteredNull = Optional.ofNullable(users)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(x -> x.getFriend() != null);

        return userStreamFilteredNull.filter(u -> id.equals(u.getId()) || otherId.equals(u.getId()))
                .map(User::getFriend)
                .flatMap(Collection::stream).map(Friend::getFriendId)
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