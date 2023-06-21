package ru.vk.etcos.tasklist.auth.config;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;
import ru.vk.etcos.tasklist.auth.service.*;

@Configuration
public class CAuthenticationManager implements AuthenticationManager {
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public CAuthenticationManager(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // кодировщик паролей односторонним алгоритмом хэширования BCrypt
    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());

        if (passwordEncoder().matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword());
        } else {
            throw new BadCredentialsException("Wrong password");
        }
    }
}
