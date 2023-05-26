package ru.vk.etcos.tasklist.auth.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import ru.vk.etcos.tasklist.auth.entity.*;

@Repository
public interface UserRepo extends JpaRepository<CUser, Long> {

}
