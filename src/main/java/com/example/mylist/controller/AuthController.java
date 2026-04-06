package com.example.mylist.controller;

import com.example.mylist.dto.UserProfileResponse;
import com.example.mylist.security.AuthenticatedUserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticatedUserService authenticatedUserService;

    public AuthController(AuthenticatedUserService authenticatedUserService) {
        this.authenticatedUserService = authenticatedUserService;
    }

    @GetMapping("/me")
    public UserProfileResponse currentUser(Authentication authentication) {
        return authenticatedUserService.profile(authentication);
    }
}
