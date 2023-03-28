package ru.vk.etcos.tasklist.auth.entity;

import jakarta.persistence.*;
import lombok.*;

// Роль пользователя, ролей у пользователя может быть несколько

@Entity
@Table(name = "ROLE_DATA", schema = "tasklist", catalog = "postgres")
@Getter
@Setter
public class CRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

}
