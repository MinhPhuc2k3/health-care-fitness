package com.health_fitness.controllers;
import com.health_fitness.model.user.User;
import com.health_fitness.config.security.CustomUserDetails;
import com.health_fitness.config.security.CustomUserDetailsService;
import com.health_fitness.config.security.JwtService;
import com.health_fitness.services.UserService;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {
    private  final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/signup")
    public User signUp(@RequestBody User user){
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody User user){
        return userService.login(user);
    }

    @PostMapping("/change-password")
    public void changePassword(@RequestBody User user){
        userService.changePassword(user);
    }
}
