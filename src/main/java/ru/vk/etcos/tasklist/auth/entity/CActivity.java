package ru.vk.etcos.tasklist.auth.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.*;

// Активность пользователя(активация и др)

@Entity
@Table(name = "ACTIVITY", schema = "tasklist", catalog = "postgres")
@Getter
@Setter
public class CActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // для автоматической конвертации числа в true/false
    // становится true только после активации пользователя
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    private boolean activated;

    // создается только один раз
    @NotBlank
    @Column(name = "uuid", updatable = false)
    private String uuid;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private CUser user;

}
