package ru.vk.etcos.tasklist.business.repository;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import ru.vk.etcos.tasklist.business.entity.*;

@Repository
public interface TaskRepo extends JpaRepository<CTask, Long> {

    List<CTask> findByUserEmailOrderByTitleAsc(String email);

}
