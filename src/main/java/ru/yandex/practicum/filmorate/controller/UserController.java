package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
        log.info("Выполнен запрос /update на обновление пользователя");
        return userService.updateUser(user);
    }

    //получение пользователя по id
    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable("userId") Integer userId) {
        log.info("Выполнен запрос /get на получение пользователя по id");
        return userService.findUserById(userId);
    }

    //добавление в друзья
    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId){
        log.info("Выполнен запрос /put на добавление пользователя в друзья");
        userService.addFriend(id,friendId);
    }

    //удаление из друзей
    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId){
        log.info("Выполнен запрос /delete на удаление пользователя из друзей");
        userService.deleteFriend(id,friendId);
    }

    //возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/user/{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") Integer Id) {
        log.info("Выполнен запрос /get на получение друзей пользователя по id");
        return userService.findUserFriendsById(Id);
    }

    //список друзей, общих с другим пользователем.
    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getSameUsersFriends(@PathVariable("id") Integer id,
                                          @PathVariable("otherId") Integer otherId) {
        log.info("Выполнен запрос /get на получение списка друзей, общих с другим пользователем.");
        return userService.findSameUsersFriends(id, otherId);
    }

}