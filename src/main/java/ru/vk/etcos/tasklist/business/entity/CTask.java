package ru.vk.etcos.tasklist.business.entity;

import java.sql.*;

import jakarta.persistence.*;
import lombok.*;
import ru.vk.etcos.tasklist.auth.entity.*;

@Entity
@Table(name = "TASK", schema = "tasklist", catalog = "postgres")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class CTask {

    // указываем что поле заполняется в БД нужно,
    // когда добавляем новый объект и он возвращается уже с новым id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    // 1 = true, 0 = false
    @Column(name = "completed")
    private Integer completed;

    @Column(name = "task_date")
    private Date taskDate;

    // ссылка на объект Priority
    // задача может иметь только один приоритет, один и тот же приоритет может использоваться во множестве задач
    // referencedColumnName = "id" : по каким полям связывать (foreign key)
    @ManyToOne
    @JoinColumn(name = "priority_id", referencedColumnName = "id")
    private CPriority priority;

    // ссылка на объект Category
    // задача может иметь только одну категорию, одна и та же категория может использоваться во множестве задач
    // referencedColumnName = "id" : по каким полям связывать (foreign key)
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private CCategory category;

    // ссылка на объект User
    // задача принадлежит только одному юзеру, один юзер может иметь множество задач
    // referencedColumnName = "id" : по каким полям связывать (foreign key)
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private CUser user;

}
