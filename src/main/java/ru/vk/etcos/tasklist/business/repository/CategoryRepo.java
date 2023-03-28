package ru.vk.etcos.tasklist.business.repository;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;
import ru.vk.etcos.tasklist.business.entity.*;

/*
Реализует все необходимые запросы к БД.

При запуске проекта Spring находит все переменные, которые имеют тип этого интерфейса.
После этого внедряет в эти переменные нужные объекты, где уже реализованы все методы.
 */

@Repository
public interface CategoryRepo extends JpaRepository<CCategory, Long> {

    // поиск категорий пользователя по емайлу(email) и сортировкой по заголовку(title)
    // Spring сам составит SQL запрос к БД основываясь на ключевых словах(find by order asc ...) в названии метода
    List<CCategory> findByUserEmailOrderByTitleAsc(String email);

    // поиск значений по названию для конкретного пользователя (JPQL - Java Persistence Query Language)
    @Query("SELECT c FROM CCategory c WHERE " +
        "(:title IS NULL OR :title = '' OR LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))) " + // если title пустой, то выберутся все записи, если не пустой - то только совпадающие записи
        "AND c.user.email = :email " + // фильтрация для конкретного пользователя
        "ORDER BY c.title ASC") // сортировка по названию
    List<CCategory> findByValues(@Param("title") String title, @Param("email") String email);
}
