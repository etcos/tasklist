package ru.vk.etcos.tasklist.auth.repository;

import java.util.*;

import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;
import ru.vk.etcos.tasklist.auth.entity.*;

@Repository
public interface RoleRepo  extends CrudRepository<CRole, Long> {

    Optional<CRole> findByName(String name);
}
