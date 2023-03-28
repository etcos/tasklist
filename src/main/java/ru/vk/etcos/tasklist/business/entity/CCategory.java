package ru.vk.etcos.tasklist.business.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.vk.etcos.tasklist.auth.entity.*;

@Entity
@Table(name = "CATEGORY", schema = "tasklist", catalog = "postgres")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class CCategory {

    // указываем что поле заполняется в БД нужно, когда добавляем новый объект и он возвращается уже с новым id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    // updatable = false : поле вычисляется автоматически в триггерах, вручную не обновляем
    @Column(name = "completed_count", updatable = false)
    private Long completedCount;

    // updatable = false : поле вычисляется автоматически в триггерах, вручную не обновляем
    @Column(name = "uncompleted_count", updatable = false)
    private Long uncompletedCount;

    // referencedColumnName = "id" : по каким полям связывать (foreign key)
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private CUser user;

}
