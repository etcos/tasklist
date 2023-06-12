package ru.vk.etcos.tasklist.auth.service;

import java.util.*;
import java.util.stream.*;

import lombok.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import ru.vk.etcos.tasklist.auth.entity.*;

/*
Класс хранит данные пользователь в Spring контейнере.
Должен реализовывать интерфейс UserDetails, чтобы Spring "принимал" этот класс.
Можно просто добавить интерфейс UserDetails в entity класс CUser (чтобы не создавать текущий класс),
но это не рекомендуется.
 */

@Getter
@Setter
public class UserDetailsImpl implements UserDetails {

    private CUser user;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(CUser user) {
        this.user = user;

        authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());
    }

    public long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public boolean isActivated() {
        return user.getActivity().isActivated();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
