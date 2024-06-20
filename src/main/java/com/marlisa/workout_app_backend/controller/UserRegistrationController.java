package com.marlisa.workout_app_backend.controller;
//Handles registration for new users using form fill.


import com.marlisa.workout_app_backend.dto.SignupRequest;
import com.marlisa.workout_app_backend.entity.User;
import com.marlisa.workout_app_backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserRegistrationController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        User user = new User();
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(signupRequest.getPassword()); // Hash the password
        user.setGender(signupRequest.getGender());
        user.setAge(signupRequest.getAge());
        user.setCurrentWeight(signupRequest.getWeight());
        user.setHowDidYouFindUs(signupRequest.getHowDidYouFindUs());
        user.setProvider("local");

        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully");
    }
}
