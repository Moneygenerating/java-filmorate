package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.Accessors;
import net.minidev.json.annotate.JsonIgnore;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class User {
    private Integer id;
    //валидация через аннотацию
    @NotBlank
    private String login;

    private String name;
    @Email
    private String email;

    @Past
    private LocalDate birthday;
    @JsonIgnore
    private Set<Integer> friendIdSet = new HashSet<>();
    //для друга
    @JsonIgnore
    private Set<Friend> friend;

    @JsonIgnore
    private Set<Likes> userLikes;

    //toDO убрать левые поля
    public User(int user_id, String login, String name, String email, LocalDate birthday) {
        this.id = user_id;
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}