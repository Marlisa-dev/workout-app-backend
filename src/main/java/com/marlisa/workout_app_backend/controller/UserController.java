package com.marlisa.workout_app_backend.controller;
//Handles general user-related operations like fetching user details, updating user profile, delete user etc.


import com.marlisa.workout_app_backend.dto.UserUpdateRequest;
import com.marlisa.workout_app_backend.entity.AppUser;
import com.marlisa.workout_app_backend.security.UserDetailsImpl;
import com.marlisa.workout_app_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint to get current user details
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        AppUser appUser = userService.getUserById(userId);
        return ResponseEntity.ok(appUser);
    }

    // Endpoint to update user details
    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest request, Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        AppUser updatedAppUser = userService.updateUser(userId, request);
        return ResponseEntity.ok(updatedAppUser);
    }

    // Optional: Endpoint to delete user account
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        userService.deleteUser(userId);
        return ResponseEntity.ok("User account deleted successfully");
    }
}
