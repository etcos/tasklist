package ru.vk.etcos.tasklist.auth.service;

import jakarta.transaction.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import ru.vk.etcos.tasklist.auth.entity.*;
import ru.vk.etcos.tasklist.auth.repository.*;

@Service
@Transactional
public class UserService {
    private UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
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
}
