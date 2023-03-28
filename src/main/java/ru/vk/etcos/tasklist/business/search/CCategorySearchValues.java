package ru.vk.etcos.tasklist.business.search;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CCategorySearchValues { // содержит значения, по которым возможен поиск категории; можно добавлять любые поля

    private String title; // такое же значение должно быть у объекта на фронте
    private String email; // для фильтрации значений конкретного пользователя

}
