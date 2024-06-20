package com.marlisa.workout_app_backend.controller;
//Handles OAuth login for Facebook and Google.

import com.marlisa.workout_app_backend.entity.User;
import com.marlisa.workout_app_backend.service.OAuthService;
import com.marlisa.workout_app_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserService userService;

    @GetMapping("/login/{provider}")
    public void oauthLogin(@PathVariable String provider, HttpServletResponse response) throws IOException {
        String redirectUrl = oAuthService.getAuthorizationUrl(provider);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/callback/{provider}")
    public ResponseEntity<?> oauthCallback(@PathVariable String provider, @RequestParam String code) {
        OAuth2User oAuth2User = oAuthService.processOAuthPostLogin(provider, code);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Extract user details
        String email = (String) attributes.get("email");
        String firstName = (String) attributes.get("given_name");
        String lastName = (String) attributes.get("family_name");

        // Create or update user in your system
        User user = userService.findOrCreateUser(email, firstName, lastName, provider, attributes);

        // Generate JWT token for the user and return it
        String jwt = userService.generateToken(user);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
}
