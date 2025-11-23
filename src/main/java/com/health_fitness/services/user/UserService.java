package com.health_fitness.services.user;


import com.health_fitness.config.security.CustomUserDetails;
import com.health_fitness.config.security.CustomUserDetailsService;
import com.health_fitness.config.security.JwtService;
import com.health_fitness.exception.AuthException;
import com.health_fitness.exception.NotFoundException;
import com.health_fitness.model.user.User;
import com.health_fitness.repository.user.UserRepository;
import jakarta.annotation.security.PermitAll;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private  final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authManager;
    private  final JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, CustomUserDetailsService userDetailsService, AuthenticationManager authManager, JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @PermitAll
    public User registerUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail()).isPresent()){
            throw new AuthException("Username is existed");
        };
        user.setJoinDate(LocalDateTime.now());
        return userRepository.save(user);
    }

    @PreAuthorize("isAuthenticated()")
    public void changePassword(User user) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userDetails.getUser();
        currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(currentUser);
    }

    @PermitAll
    public User login(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsernameOrEmail(user.getUsername(), user.getEmail());
        authManager.authenticate(new UsernamePasswordAuthenticationToken(userDetails, user.getPassword()));
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        user = customUserDetails.getUser();
        String token = jwtService.generateToken(user.getUsername(), user.getEmail());
        user.setToken(token);
        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }

    @PreAuthorize("isAuthenticated()")
    public User getUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getUser().getId()).orElseThrow(()-> new NotFoundException("User not found"));
    }
}
