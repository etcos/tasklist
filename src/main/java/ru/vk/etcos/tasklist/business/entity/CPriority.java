package ru.vk.etcos.tasklist.business.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.vk.etcos.tasklist.auth.entity.*;

@Entity
@Table(name = "PRIORITY", schema = "tasklist", catalog = "postgres")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class CPriority {

    // указываем что поле заполняется в БД нужно, когда добавляем новый объект и он возвращается уже с новым id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "color", nullable = false)
    private String color;

    // referencedColumnName = "id" : по каким полям связывать (foreign key)
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private CUser user;

}
