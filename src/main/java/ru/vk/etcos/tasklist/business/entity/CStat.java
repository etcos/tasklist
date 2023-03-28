package ru.vk.etcos.tasklist.business.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.vk.etcos.tasklist.auth.entity.*;

// в этой таблице всего 1 запись, которая обновляется (никогда не удаляется)
@Entity
@Table(name = "STAT", schema = "tasklist", catalog = "postgres")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class CStat {

    @Id
    private Long id;

    // updatable = false : поле вычисляется автоматически в триггерах, вручную не обновляем
    @Column(name = "completed_total", updatable = false)
    private Long completedTotal;

    // updatable = false : поле вычисляется автоматически в триггерах, вручную не обновляем
    @Column(name = "uncompleted_total", updatable = false)
    private Long uncompletedTotal;

    // referencedColumnName = "id" : по каким полям связывать (foreign key)
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private CUser user;

}
