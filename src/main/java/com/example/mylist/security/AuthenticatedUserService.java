package com.example.mylist.security;

import com.example.mylist.dto.UserProfileResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    public String email(Authentication authentication) {
        OAuth2User user = oauth2User(authentication);
        String email = user.getAttribute("email");
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Authenticated user email is missing");
        }
        return email;
    }

    public UserProfileResponse profile(Authentication authentication) {
        OAuth2User user = oauth2User(authentication);
        String name = valueOrDefault(user.getAttribute("name"), email(authentication));
        String email = email(authentication);
        String picture = user.getAttribute("picture");
        return new UserProfileResponse(name, email, picture);
    }

    private OAuth2User oauth2User(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oauth2User) {
            return oauth2User;
        }
        throw new IllegalStateException("Authenticated principal is not an OAuth2 user");
    }

    private String valueOrDefault(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
