package ru.vk.etcos.tasklist.business.repository;

import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;
import ru.vk.etcos.tasklist.business.entity.*;

@Repository
public interface StatRepo extends CrudRepository<CStat, Long> {

    // Возвращается только 1 запись (каждый пользователь содержит только 1 запись в таблице)
    CStat findByUserEmail(String email);

}
