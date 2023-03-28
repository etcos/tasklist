package ru.vk.etcos.tasklist.auth.entity;

import java.util.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

// Объект не имеет прямых связей с другими объектами бизнес-процесса.

@Entity
@Table(name = "USER_DATA", schema = "tasklist", catalog = "postgres")
@Getter
@Setter
public class CUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // встроенный валидатор email'ов
    @Email
    @Column(name = "email", nullable = false)
    private String email;

    // пароль желательно сразу занулять после авторизации (в контроллере)
    @Column(name = "password", nullable = false, length = -1)
    private String password;

    @Column(name = "username", nullable = false)
    private String username;

    // обратная ссылка - указываем поле "user" из Activity, которое ссылается на User
    // Activity имеет ключ на User
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private CActivity activity;

    // таблица role ссылается на user через промежуточную таблицу user_role
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_ROLE", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<CRole> roles = new HashSet<>();

    // для сравнения объектов User между собой (если email равны - значит объекты тоже равны)
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CUser user = (CUser) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
