package com.health_fitness.controllers;
import com.health_fitness.model.user.User;
import com.health_fitness.services.user.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private  final UserService userService;

    public AuthController(UserService userService){
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
