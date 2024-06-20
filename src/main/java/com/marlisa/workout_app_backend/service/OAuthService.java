package com.marlisa.workout_app_backend.service;

import com.marlisa.workout_app_backend.entity.User;
import com.marlisa.workout_app_backend.repository.UserRepository;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Service
public class OAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    public String getAuthorizationUrl(String provider) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(provider);
        return clientRegistration.getProviderDetails().getAuthorizationUri();
    }

    public OAuth2User processOAuthPostLogin(String provider, String code) {
        OAuth2UserRequest userRequest = createOAuth2UserRequest(provider, code);
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String firstName = (String) attributes.get(getFirstNameAttributeKey(provider));
        String lastName = (String) attributes.get(getLastNameAttributeKey(provider));

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
            user.setProviderId((String) attributes.get("sub")); // Example for Google, might need a different attribute for Facebook
            userRepository.save(user);
        }

        return oAuth2User;
    }

    private OAuth2UserRequest createOAuth2UserRequest(String provider, String code) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(provider);
        OAuth2AccessToken accessToken = obtainAccessToken(clientRegistration, code);
        return new OAuth2UserRequest(clientRegistration, accessToken);
    }

    private OAuth2AccessToken obtainAccessToken(ClientRegistration clientRegistration, String code) {
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "authorization_code");
        body.put("code", code);
        body.put("redirect_uri", clientRegistration.getRedirectUri());
        body.put("client_id", clientRegistration.getClientId());
        body.put("client_secret", clientRegistration.getClientSecret());

        Map<String, Object> response = restTemplate.postForObject(tokenUri, body, Map.class);
        String accessTokenValue = (String) response.get("access_token");

        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessTokenValue, null, null);
    }

    private String getFirstNameAttributeKey(String provider) {
        switch (provider) {
            case "google":
                return "given_name";
            case "facebook":
                return "first_name";
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }

    private String getLastNameAttributeKey(String provider) {
        switch (provider) {
            case "google":
                return "family_name";
            case "facebook":
                return "last_name";
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }
}
