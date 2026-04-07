package com.example.mylist.application.dto;

public record UserProfileResponse(
        String name,
        String email,
        String picture
) {
}
