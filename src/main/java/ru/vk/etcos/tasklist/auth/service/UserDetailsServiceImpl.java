package ru.vk.etcos.tasklist.auth.service;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import ru.vk.etcos.tasklist.auth.entity.*;
import ru.vk.etcos.tasklist.auth.repository.*;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepo userRepo;

    @Autowired
    public UserDetailsServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<CUser> userOpt = userRepo.findByUsername(username);

        if (userOpt.isEmpty()) {
            userOpt = userRepo.findByEmail(username);
        }

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User Not Found with username or email: " + username);
        }

        return new UserDetailsImpl(userOpt.get());
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        Optional<CUser> userOpt = userRepo.findById(id);

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User Not Found with id: " + id);
        }

        return new UserDetailsImpl(userOpt.get());
    }
}
