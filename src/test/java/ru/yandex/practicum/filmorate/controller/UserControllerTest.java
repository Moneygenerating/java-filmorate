package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dao.impl.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    InMemoryUserStorage inMemoryUserStorage;
    UserService userService;
    UserController userController;
    User user;
    User user2;

    @BeforeEach
    void init() {
        inMemoryUserStorage = new InMemoryUserStorage();
        userService = new UserService(inMemoryUserStorage);
        userController = new UserController(userService);

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
    void findAll() {
        userController.createUser(user);
        userController.createUser(user2);
        assertEquals(2, userController.findAll().size());
    }

    @Test
    void createUser() {
        userController.createUser(user);
        assertEquals(user, userController.getUser(1));
    }

    @Test
    void updateUser() {
        userController.createUser(user);

        User userUpdatable = new User()
                .setId(1)
                .setName("The ShadowUPD")
                .setLogin("SteveT")
                .setEmail("Steve@gmail.com")
                .setBirthday(LocalDate.of(1993, Month.APRIL, 20));

        userController.updateUser(userUpdatable);
        assertEquals(userUpdatable, userController.getUser(1));
    }

    @Test
    void getUser() {
        userController.createUser(user);
        userController.createUser(user2);

        assertEquals(user, userController.getUser(1));
        assertEquals(user2, userController.getUser(2));
    }
}