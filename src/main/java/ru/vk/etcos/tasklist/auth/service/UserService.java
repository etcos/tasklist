package ru.vk.etcos.tasklist.auth.service;

import java.util.*;

import jakarta.transaction.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import ru.vk.etcos.tasklist.auth.entity.*;
import ru.vk.etcos.tasklist.auth.repository.*;

@Service
@Transactional
public class UserService {
    public static final String DEFAULT_ROLE = "USER";

    private UserRepo userRepo;
    private RoleRepo roleRepo;

    @Autowired
    public UserService(UserRepo userRepo, RoleRepo roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    public CUser save(CUser user) {
        return userRepo.save(user);
    }

    public boolean userExistsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public boolean userExistsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    public Optional<CRole> findByName(String role) {
        return roleRepo.findByName(role);
    }
}
