package com.marlisa.workout_app_backend.controller;

import com.marlisa.workout_app_backend.dto.SignupRequest;
import com.marlisa.workout_app_backend.entity.AppUser;
import com.marlisa.workout_app_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserRegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        AppUser appUser = new AppUser();
        appUser.setFirstName(signupRequest.getFirstName());
        appUser.setLastName(signupRequest.getLastName());
        appUser.setUsername(signupRequest.getUsername());
        appUser.setEmail(signupRequest.getEmail());
        appUser.setPassword(passwordEncoder.encode(signupRequest.getPassword())); // Hash the password
        appUser.setGender(signupRequest.getGender());
        appUser.setAge(signupRequest.getAge());
        appUser.setCurrentWeight(signupRequest.getWeight());
        appUser.setHowDidYouFindUs(signupRequest.getHowDidYouFindUs());
        appUser.setProvider("local");

        userService.saveUser(appUser);
        return ResponseEntity.ok("User registered successfully");
    }
}
