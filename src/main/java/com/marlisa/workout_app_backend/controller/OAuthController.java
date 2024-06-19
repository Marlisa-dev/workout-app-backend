package com.marlisa.workout_app_backend.controller;
//Handles OAuth login for Facebook and Google.

import com.marlisa.workout_app_backend.entity.User;
import com.marlisa.workout_app_backend.service.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @GetMapping("/login/{provider}")
    public void oauthLogin(@PathVariable String provider, HttpServletResponse response) throws IOException {
        String redirectUrl = oAuthService.getAuthorizationUrl(provider);
        response.sendRedirect(redirectUrl);
    }


    @GetMapping("/callback/{provider}")
    public ResponseEntity<?> oauthCallback(@PathVariable String provider, @RequestParam String code) {
        OAuth2User oAuth2User = oAuthService.processOAuthPostLogin(provider, code);

        // Extract additional data from OAuth2User and save to database
        User user = new User();
        user.setEmail(oAuth2User.getEmail());
        user.setProvider(provider);
        user.setProviderId(oAuth2User.getName());
        user.setGender(oAuth2User.getAttribute("gender"));
        user.setAge(Integer.parseInt(oAuth2User.getAttribute("age")));
        user.setWeight(Float.parseFloat(oAuth2User.getAttribute("weight")));
        user.setHowYouFound(oAuth2User.getAttribute("howYouFound"));

        userService.saveUser(user);

        // Generate JWT and return
        String jwt = tokenProvider.generateToken(user);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }


}
