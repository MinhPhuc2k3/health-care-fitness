package com.health_fitness.config.security;

import com.health_fitness.model.user.User;
import com.health_fitness.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepository = userRepo;
    }

    public UserDetails loadUserByUsernameOrEmail(String username, String email) {
        User user = userRepository.findByUsernameOrEmail(username, email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(username, null)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }
}
