package ru.vk.etcos.tasklist.business.repository;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import ru.vk.etcos.tasklist.business.entity.*;

@Repository
public interface PriorityRepo extends JpaRepository<CPriority, Long> {

    List<CPriority> findByUserEmailOrderByIdAsc(String email);
}
