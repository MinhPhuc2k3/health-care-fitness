package com.health_fitness.controllers.user;

import com.health_fitness.model.user.User;
import com.health_fitness.services.user.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("api/v1/user")
public class UserController {
    private  final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public User getUserInfo(){
        return userService.getUser();
    }
}
