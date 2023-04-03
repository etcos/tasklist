package ru.vk.etcos.tasklist.business.repository;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;
import ru.vk.etcos.tasklist.business.entity.*;

@Repository
public interface PriorityRepo extends JpaRepository<CPriority, Long> {

    List<CPriority> findByUserEmailOrderByIdAsc(String email);

    @Query("SELECT p FROM CPriority p WHERE " +
        "(:title IS NULL OR :title = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')))" +
        "AND p.user.email = :email " +
        "ORDER BY p.title ASC")
    List<CPriority> findByValues(@Param("title") String title, @Param("email") String email);
}
