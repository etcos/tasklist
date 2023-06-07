package ru.vk.etcos.tasklist.auth.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.vk.etcos.tasklist.auth.entity.*;

@Repository
public interface ActivityRepo extends JpaRepository<CActivity, Long> {
}
