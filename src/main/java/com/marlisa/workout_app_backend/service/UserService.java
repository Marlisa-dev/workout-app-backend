package com.marlisa.workout_app_backend.service;

import com.marlisa.workout_app_backend.entity.User;
import com.marlisa.workout_app_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User registerUser(SignupRequest signupRequest) {
        User user = new User();
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setGender(signupRequest.getGender());
        user.setAge(signupRequest.getAge());
        user.setWeight(signupRequest.getWeight());
        user.setHowDidYouFindUs(signupRequest.getHowDidYouFindUs());
        user.setProvider("local");

        return userRepository.save(user);
    }

    public User processOAuthPostLogin(OAuth2User oAuth2User, String provider) {
        Optional<User> existingUser = userRepository.findByEmail(oAuth2User.getAttribute("email"));
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User user = new User();
        user.setFirstName(oAuth2User.getAttribute("given_name"));
        user.setLastName(oAuth2User.getAttribute("family_name"));
        user.setEmail(oAuth2User.getAttribute("email"));
        user.setProvider(provider);
        user.setProviderId(oAuth2User.getAttribute("sub"));

        return userRepository.save(user);
    }

    public String generateToken(User user) {
        // Generate JWT token for the user
        // Implement JWT token generation logic here
        return "jwt-token";
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUser(Long id, UserUpdateRequest request) {
        User user = getUserById(id);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        user.setWeight(request.getWeight());
        user.setHowDidYouFindUs(request.getHowDidYouFindUs());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User getCurrentUser(Authentication authentication) {
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return getUserById(userId);
    }
}
