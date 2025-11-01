package com.health_fitness.config.security;

import com.health_fitness.model.user.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> permissions = new HashSet<>();

        // Từ mỗi role => lấy danh sách permission
        for (Role role : user.getRoles()) {
            permissions.add("ROLE_" + role.getRole().toUpperCase()); // bắt buộc thêm ROLE_
            for (Permission p : role.getPermissions()) {
                permissions.add(p.getPermission().toUpperCase());
            }
        }

        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public User getUser() { return user; }
}
