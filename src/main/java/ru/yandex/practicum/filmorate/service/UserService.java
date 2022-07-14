package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserBirthdayException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();

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
}