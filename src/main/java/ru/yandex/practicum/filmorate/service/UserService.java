package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserBirthdayException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer usersId = 0;

    public Collection<User> findAll() {
        return users.values();
    }

    public User createUser(User user) {
        checkEmail(user);
        validateBirthdayAndName(user);
        if (users.containsKey(user.getId())) {
            throw new UserAlreadyExistException(String.format(
                    "Пользователь с таким id %s уже зарегистрирован.",
                    user.getId()
            ));
        }
        user.setId(++usersId);
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        checkEmail(user);
        validateBirthdayAndName(user);
        if(!users.containsKey(user.getId())){
            throw new UserAlreadyExistException(String.format(
                    "Пользователь с id %s не найден.",
                    user.getId()
            ));
        }
        users.put(user.getId(), user);

        return user;
    }

    public User findUserByEmail(Integer id) {
        if (id == null) {
            return null;
        }
        return users.get(id);
    }

    private void checkEmail(User user) {
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
        if(user.getName() == ""){
            user.setName(user.getLogin());
        }
    }
}