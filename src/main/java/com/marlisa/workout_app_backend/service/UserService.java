package com.marlisa.workout_app_backend.service;

import com.marlisa.workout_app_backend.dto.SignupRequest;
import com.marlisa.workout_app_backend.dto.UserUpdateRequest;
import com.marlisa.workout_app_backend.entity.User;
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

    public User registerUser(SignupRequest signupRequest) {
        User user = new User();
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setGender(signupRequest.getGender());
        user.setAge(signupRequest.getAge());
        user.setCurrentWeight(signupRequest.getWeight());
        user.setHowDidYouFindUs(signupRequest.getHowDidYouFindUs());
        user.setProvider("local");

        return userRepository.save(user); //save the new user to the database
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }


    public User findOrCreateUser(String email, String firstName, String lastName, String provider, Map<String, Object> attributes) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setProvider(provider);
            user.setProviderId((String) attributes.get("sub")); // Example for Google
            userRepository.save(user);
        }
        return user;
    }

    public String generateToken(User user) {
        // Implement JWT token generation logic here
        return "jwt-token";
    }

    public User updateUser(Long id, UserUpdateRequest request) {
        User user = getUserById(id);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        user.setCurrentWeight(request.getWeight());
        user.setHowDidYouFindUs(request.getHowDidYouFindUs());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User getCurrentUser(Long userId) {
        return getUserById(userId);
    }

    public void sendPasswordResetLink(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();
        // Save the token and its expiration time in the database
        // Implement logic to save the token (not shown here, you need to add token management)

        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        String resetUrl = "http://localhost:8080/api/auth/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n" + resetUrl);
        mailSender.send(message);
    }

    public void updatePassword(String token, String newPassword) {
        // Validate the token and get the user associated with it
        // Implement logic to validate the token (not shown here, you need to add token management)

        Optional<User> userOptional = userRepository.findByResetPasswordToken(token);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("Invalid token");
        }

        User user = userOptional.get();
        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

//        User user = userRepository.findByResetPasswordToken(token)
//                .orElseThrow(() -> new RuntimeException("Invalid token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        // Remove the reset password token from the database
        // Implement logic to remove the token (not shown here, you need to add token management)
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);

        userRepository.save(user);
    }

    public Authentication authenticateUser(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

}
