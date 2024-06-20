package com.marlisa.workout_app_backend.service;
import com.marlisa.workout_app_backend.entity.User;
import com.marlisa.workout_app_backend.repository.UserRepository;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.util.Optional;

@Service
public class OAuthService {

    @Autowired
    private UserRepository userRepository;

    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    public String getAuthorizationUrl(String provider) {
        // Generate the authorization URL for the OAuth provider
        // Implement logic to return the appropriate authorization URL based on the provider
        return "authorization-url";
    }

    public OAuth2User processOAuthPostLogin(String provider, String code) {
        // Exchange the authorization code for an OAuth2UserRequest
        OAuth2UserRequest userRequest = exchangeCodeForUserRequest(provider, code);
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

        // Retrieve user attributes
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String firstName = (String) attributes.get("given_name");
        String lastName = (String) attributes.get("family_name");

        // Create or update user in your system using these attributes
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

        return oAuth2User;
    }

    private OAuth2UserRequest exchangeCodeForUserRequest(String provider, String code) {
        // Implement your logic to create OAuth2UserRequest from authorization code
        // This is a placeholder and should be replaced with actual implementation
        // Depending on your OAuth2 provider, you might need to make a request to exchange the code for a token

        // Example placeholder logic (replace with actual logic)
        return new OAuth2UserRequest(
                // your OAuth2 client registration,
                // your OAuth2 access token response
        );
    }
}
