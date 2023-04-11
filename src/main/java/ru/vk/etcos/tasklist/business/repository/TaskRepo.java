package ru.vk.etcos.tasklist.business.repository;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;
import ru.vk.etcos.tasklist.business.entity.*;

@Repository
public interface TaskRepo extends JpaRepository<CTask, Long> {

    List<CTask> findByUserEmailOrderByTitleAsc(String email);

    @Query("SELECT t FROM CTask t WHERE " +
        "(:title IS NULL OR :title = '' OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
        "(:comleted IS NULL OR t.completed = :comleted) AND " +
        "(:priorityId IS NULL OR t.priority.id = :priorityId) AND " +
        "(:categoryId IS NULL OR t.category.id = :categoryId) AND " +
        "((CAST(:dateFrom AS TIMESTAMP) IS NULL OR t.taskDate >= :dateFrom) AND " +
        "(CAST(:dateTo AS TIMESTAMP) IS NULL OR t.taskDate <= :dateTo)) AND " +
        "(t.user.email = :email)"
    )
    Page<CTask> findByValues(
        @Param("title") String title,
        @Param("completed") Integer completed,
        @Param("priorityId") Long priorityId,
        @Param("categoryId") Long categoryId,
        @Param("email") String email,
        @Param("dateFrom") Date dateFrom,
        @Param("dateTo") Date dateTo,
        Pageable pageable
    );
}
