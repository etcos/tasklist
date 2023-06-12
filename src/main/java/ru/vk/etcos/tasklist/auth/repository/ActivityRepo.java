package ru.vk.etcos.tasklist.auth.repository;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.*;
import ru.vk.etcos.tasklist.auth.entity.*;

@Repository
public interface ActivityRepo extends CrudRepository<CActivity, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE CActivity a SET a.activated = :active WHERE a.uuid = :uuid")
    int changeActivated(@Param("uuid") String uuid, @Param("active") boolean active);

    Optional<CActivity> findByUuid(String uuid);

    Optional<CActivity> findByUserId(long id);
}
