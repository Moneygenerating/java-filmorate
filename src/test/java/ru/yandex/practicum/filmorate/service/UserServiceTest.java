package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserBirthdayException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void validate() {
        final UserService userService = new UserService();
        final User user = new User();
        user.setBirthday(LocalDate.MAX);
        assertThrows(UserBirthdayException.class, () -> userService.validateBirthdayAndName(user));
    }
}