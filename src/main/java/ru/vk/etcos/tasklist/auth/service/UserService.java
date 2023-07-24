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
    private ActivityRepo activityRepo;

    @Autowired
    public UserService(UserRepo userRepo, RoleRepo roleRepo, ActivityRepo activityRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.activityRepo = activityRepo;
    }

    public CUser register(CUser user, CActivity activity) {
        CUser savedUser = userRepo.save(user);
        activity.setUser(savedUser);

        CActivity savedActivity = activityRepo.save(activity);
        user.setActivity(savedActivity);

        return savedUser;
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

    public Optional<CActivity> findActivityByUuid(String uuid) {
        return activityRepo.findByUuid(uuid);
    }

    public int activate(String uuid) {
        return activityRepo.changeActivated(uuid, true);
    }

    public int deactivate(String uuid) {
        return activityRepo.changeActivated(uuid, false);
    }

    public int updatePassword(String password, String username) {
        return userRepo.updatePassword(password, username);
    }
}
