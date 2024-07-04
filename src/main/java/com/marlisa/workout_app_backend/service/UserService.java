package com.marlisa.workout_app_backend.service;

import com.marlisa.workout_app_backend.dto.SignupRequest;
import com.marlisa.workout_app_backend.dto.UserUpdateRequest;
import com.marlisa.workout_app_backend.entity.AppUser;
import com.marlisa.workout_app_backend.repository.UserRepository;
import com.marlisa.workout_app_backend.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public AppUser registerUser(SignupRequest signupRequest) {
        AppUser appUser = new AppUser();
        appUser.setFirstName(signupRequest.getFirstName());
        appUser.setLastName(signupRequest.getLastName());
        appUser.setUsername(signupRequest.getUsername());
        appUser.setEmail(signupRequest.getEmail());
        appUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        appUser.setGender(signupRequest.getGender());
        appUser.setAge(signupRequest.getAge());
        appUser.setCurrentWeight(signupRequest.getWeight());
        appUser.setHowDidYouFindUs(signupRequest.getHowDidYouFindUs());
        appUser.setProvider("local");

        return userRepository.save(appUser); //save the new user to the database
    }

    public AppUser getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public AppUser saveUser(AppUser appUser) {
        return userRepository.save(appUser);
    }


    public AppUser findOrCreateUser(String email, String firstName, String lastName, String provider, Map<String, Object> attributes) {
        Optional<AppUser> existingUser = userRepository.findByEmail(email);
        AppUser appUser;
        if (existingUser.isPresent()) {
            appUser = existingUser.get();
        } else {
            appUser = new AppUser();
            appUser.setEmail(email);
            appUser.setFirstName(firstName);
            appUser.setLastName(lastName);
            appUser.setProvider(provider);
            appUser.setProviderId((String) attributes.get("sub")); // Example for Google
            userRepository.save(appUser);
        }
        return appUser;
    }

    public String generateToken(AppUser appUser) {
        // Implement JWT token generation logic here
        return "jwt-token";
    }

    public AppUser updateUser(Long id, UserUpdateRequest request) {
        AppUser appUser = getUserById(id);
        appUser.setFirstName(request.getFirstName());
        appUser.setLastName(request.getLastName());
        appUser.setUsername(request.getUsername());
        appUser.setEmail(request.getEmail());
        appUser.setGender(request.getGender());
        appUser.setAge(request.getAge());
        appUser.setCurrentWeight(request.getWeight());
        appUser.setHowDidYouFindUs(request.getHowDidYouFindUs());

        return userRepository.save(appUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public AppUser getCurrentUser(Long userId) {
        return getUserById(userId);
    }

    public void sendPasswordResetLink(String email) {
        Optional<AppUser> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }

        AppUser appUser = userOptional.get();
        String token = UUID.randomUUID().toString();
        // Save the token and its expiration time in the database
        // Implement logic to save the token (not shown here, you need to add token management)

        appUser.setResetPasswordToken(token);
        appUser.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(appUser);

        String resetUrl = "http://localhost:8080/api/auth/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(appUser.getEmail());
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n" + resetUrl);
        mailSender.send(message);
    }

    public void updatePassword(String token, String newPassword) {
        // Validate the token and get the user associated with it
        // Implement logic to validate the token (not shown here, you need to add token management)

        Optional<AppUser> userOptional = userRepository.findByResetPasswordToken(token);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("Invalid token");
        }

        AppUser appUser = userOptional.get();
        if (appUser.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

//        User user = userRepository.findByResetPasswordToken(token)
//                .orElseThrow(() -> new RuntimeException("Invalid token"));

        appUser.setPassword(passwordEncoder.encode(newPassword));
        // Remove the reset password token from the database
        // Implement logic to remove the token (not shown here, you need to add token management)
        appUser.setResetPasswordToken(null);
        appUser.setResetPasswordTokenExpiry(null);

        userRepository.save(appUser);
    }

    public Authentication authenticateUser(AppUser appUser) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(appUser.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

}
