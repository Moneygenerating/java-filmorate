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
    private HashSet<Integer> friendIds = new HashSet<>();

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