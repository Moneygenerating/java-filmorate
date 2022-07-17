package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dao.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.UserBirthdayException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    InMemoryUserStorage inMemoryUserStorage;
    UserService userService;
    User user;
    User user2;

    @BeforeEach
    void init() {
        inMemoryUserStorage = new InMemoryUserStorage();
        userService = new UserService(inMemoryUserStorage);
        user = new User()
                .setId(1)
                .setName("The Shadow")
                .setLogin("SteveT")
                .setEmail("Steve@gmail.com")
                .setBirthday(LocalDate.of(1993, Month.APRIL, 20));

        user2 = new User()
                .setId(2)
                .setName("The Shadow2")
                .setLogin("SteveD")
                .setEmail("Steve2@gmail.com")
                .setBirthday(LocalDate.of(1999, Month.APRIL, 29));
    }

    @Test
    void validateBirthdayError() {
        user.setBirthday(LocalDate.MAX);
        assertThrows(UserBirthdayException.class, () -> userService.validateBirthdayAndName(user));
    }

    @Test
    void validateNameError() {
        user.setName("");
        userService.validateBirthdayAndName(user);
        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void checkEmailError() {
        user.setEmail("");
        assertThrows(InvalidEmailException.class, () -> userService.checkEmail(user));
    }

    @Test
    void findUserByEmailError() {
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(2342342));
    }
}