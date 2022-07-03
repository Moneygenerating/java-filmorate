package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //получение всех пользователей
    @GetMapping
    public Collection<User> findAll() {
        log.info("Выполнен запрос /get на получение списка пользователей");
        return userService.findAll();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Выполнен запрос /post на создание пользователя");
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Выполнен запрос /put на обновление пользователя");
        return userService.updateUser(user);
    }

    //получение пользователя по email
    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable("userId") Integer userId) {
        log.info("Выполнен запрос /get на получение пользователя по id");
        return userService.findUserById(userId);
    }
}