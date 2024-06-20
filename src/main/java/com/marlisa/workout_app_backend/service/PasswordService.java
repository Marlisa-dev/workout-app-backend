package com.marlisa.workout_app_backend.service;

import com.marlisa.workout_app_backend.entity.User;
import com.marlisa.workout_app_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.resetPasswordTokenExpirationMs}")
    private long resetPasswordTokenExpirationMs;

    public void sendPasswordResetLink(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();
        // Save the token and its expiration time in the database
        // Implement logic to save the token

        String resetUrl = "http://localhost:8080/api/auth/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n" + resetUrl);
        mailSender.send(message);
    }

    public void updatePassword(String token, String newPassword) {
        // Validate the token and get the user associated with it
        // Implement logic to validate the token

        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        // Remove the reset password token from the database
        // Implement logic to remove the token

        userRepository.save(user);
    }
}
