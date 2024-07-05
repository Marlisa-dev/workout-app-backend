package com.marlisa.workout_app_backend.controller;
//Handles OAuth login for Facebook and Google.

import com.marlisa.workout_app_backend.dto.JwtAuthenticationResponse;
import com.marlisa.workout_app_backend.entity.AppUser;
import com.marlisa.workout_app_backend.security.JwtTokenProvider;
import com.marlisa.workout_app_backend.service.OAuthService;
import com.marlisa.workout_app_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

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
        AppUser appUser = userService.findOrCreateUser(email, firstName, lastName, provider, attributes);

        // Authenticate user and set context
        Authentication authentication = userService.authenticateUser(appUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token for the user and return it
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
}
