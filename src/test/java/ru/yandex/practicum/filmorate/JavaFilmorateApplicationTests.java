package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Order;
import ru.yandex.practicum.filmorate.dao.impl.*;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JavaFilmorateApplicationTests {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final FriendsDbStorage friendsDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @Test
    @Order(1)
    public void userDbStorageTest() {
        User user1 = new User(1, "user1", "AxelRoads", "us1@mail.ru",
                LocalDate.of(1993, 2, 23));
        User user2 = new User(2, "user2", "DollarBIL", "us2@mail.ru",
                LocalDate.of(2002, 2, 24));
        User userUpdated1 = new User(1, "usUPDATED", "UPDATED",
                "usUPDATED@mail.ru", LocalDate.of(1993, 2, 23));
        User user3 = new User(3, "MEGADOOM", "Lord Excel", "word@mail.ru",
                LocalDate.of(1989, 1, 1));


        //test saveUser and getUser
        userDbStorage.saveUser(user1);
        assertEquals(user1, userDbStorage.getUser(1));

        userDbStorage.saveUser(user2);
        Collection<User> userCollection = List.of(user1, user2);
        //getUsers
        assertEquals(userCollection, userDbStorage.getUsers());

        //test update
        userDbStorage.updateUser(userUpdated1);

        assertEquals(userUpdated1, userDbStorage.getUser(1));

        //test addFriend and getUserFriendsById
        userDbStorage.addFriend(1, 2);

        friendsDbStorage.loadFriends(user1);

        //test getUserFriendsById() and get friends
        assertEquals(List.of(user2), user1.getFriend().stream().mapToInt(Friend::getFriendId)
                .mapToObj(userDbStorage::getUser).collect(Collectors.toList()));

        friendsDbStorage.deleteUserFriends(user1);

        assertEquals(Set.of(), userDbStorage.getUserFriendsById(1));

        //test common friends тест метода findFriendsIntersection
        userDbStorage.saveUser(user3);
        userDbStorage.addFriend(1, 3);
        userDbStorage.addFriend(2, 3);

        //зададим id друзей
        Integer id = 1;
        Integer otherId = 2;

        List<User> users = userDbStorage.getUsers();
        friendsDbStorage.loadFriends(users);

        Stream<User> userStreamFilteredNull = Optional.ofNullable(users)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(x -> x.getFriend() != null);

        assertEquals(List.of(user3), userStreamFilteredNull.filter(u -> id.equals(u.getId()) || otherId.equals(u.getId()))
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
                .map(userDbStorage::getUser).collect(Collectors.toList()));
    }

    @Test
    @Order(2)
    public void filmDbStorageTest() {
        Film film1 = new Film(1, "nameFilm1", "AboutOurTest",
                100, LocalDate.of(2001, 1, 1), new Mpa(1, "Комедия"));
        Film film2 = new Film(2, "nameFilm2", "AboutOurTest2",
                200, LocalDate.of(2012, 1, 1), new Mpa(2, "Драма"));
        Film filmUpdated1 = new Film(1, "film1film1ChangedName", "film1film1ChangedDescription",
                101, LocalDate.of(2001, 2, 11), new Mpa(3, "Мультфильм"));

        filmDbStorage.saveFilm(film1);
        // тест методов saveFilm и getFilmById
        assertEquals(film1, filmDbStorage.getFilm(1));

        filmDbStorage.saveFilm(film2);
        Collection<Film> filmCollection = List.of(film1, film2);
        //test getAll
        assertEquals(filmCollection, filmDbStorage.getFilms());

        filmDbStorage.updateFilm(filmUpdated1);
        //testPutFilm
        assertEquals(filmUpdated1, filmDbStorage.getFilm(1));
    }

    @Test
    @Order(3)
    public void genreDbStorageTest() {
        Set<Genre> genreSet = Set.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик"));

        assertEquals(genreSet, genreDbStorage.getGenreAll());
        assertEquals(new Genre(2, "Драма"), genreDbStorage.getGenreById(2));
    }

    @Test
    @Order(4)
    public void mpaDbStorageTest() {
        Set<Mpa> mpaSet = Set.of(
                new Mpa(1, "G"),
                new Mpa(2, "PG"),
                new Mpa(3, "PG-13"),
                new Mpa(4, "R"),
                new Mpa(5, "NC-17"));

        assertEquals(mpaSet, mpaDbStorage.getMpaAll());
        assertEquals(new Mpa(3, "PG-13"), mpaDbStorage.getMpaById(3));
    }

    @Test
    @Order(5)
    public void likeDbStorageTest() {
        User user1 = new User(1, "user1", "AxelRoads", "us1@mail.ru",
                LocalDate.of(1993, 2, 23));
        Film film1 = new Film(1, "Film1", "AboutOurTest",
                100, LocalDate.of(2001, 1, 1), new Mpa(1, "Комедия"));

        Film film2 = new Film(2, "Film2", "AboutNormal",
                100, LocalDate.of(2011, 1, 1), new Mpa(1, "Комедия"));

        filmDbStorage.saveFilm(film1);
        filmDbStorage.saveFilm(film2);
        userDbStorage.saveUser(user1);
        filmDbStorage.addLike(1, 1);

        // test getTopFilms and AddLikes
        Film film1Changed = filmDbStorage.getTopFilms(1).stream().findAny().get();
        assertEquals(1, film1Changed.getId());

        // test AddLike
        filmDbStorage.addLike(2, 2);

        Film film1Changed2 = filmDbStorage.getFilm(2);
        likeDbStorage.loadFilmLikes(film1Changed2);

        assertEquals(2, film1Changed2.getLikes().stream().map(Likes::getFilmId).findAny().get()); // тест метода addLike
        // test DeleteLike
        likeDbStorage.deleteFilmLikes(film2);
        assertEquals(null, filmDbStorage.getFilm(film2.getId()).getLikes()); // тест метода deleteLike
    }
}


