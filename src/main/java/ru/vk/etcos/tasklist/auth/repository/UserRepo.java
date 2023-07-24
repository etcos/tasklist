package ru.vk.etcos.tasklist.auth.repository;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import ru.vk.etcos.tasklist.auth.entity.*;

@Repository
public interface UserRepo extends JpaRepository<CUser, Long> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM CUser u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM CUser u WHERE LOWER(u.username) = LOWER(:username)")
    boolean existsByUsername(@Param("username") String username);

    Optional<CUser> findByUsername(String username);

    Optional<CUser> findByEmail(String email);

    // обновление пароля
    @Modifying
    @Transactional
    @Query("UPDATE CUser u SET u.password = :password WHERE u.username = :username")
    int updatePassword(@Param("password") String password, @Param("username") String username);
}
