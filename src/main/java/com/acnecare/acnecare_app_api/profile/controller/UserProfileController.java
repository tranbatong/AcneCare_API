package com.acnecare.acnecare_app_api.profile.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import com.acnecare.acnecare_app_api.profile.service.UserProfileService;
import com.acnecare.acnecare_app_api.profile.dto.request.UserProfileCreationRequest;
import com.acnecare.acnecare_app_api.profile.dto.request.UserProfileUpdateRequest;
import com.acnecare.acnecare_app_api.profile.dto.response.UserProfileResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {
    UserProfileService userProfileService;

    @PostMapping
    public UserProfileResponse createUserProfile(@RequestBody UserProfileCreationRequest request) {
        return userProfileService.createUserProfile(request);
    }

    @PutMapping("/{id}")
    public UserProfileResponse updateUserProfile(@PathVariable String id, @RequestBody UserProfileUpdateRequest request) {
        return userProfileService.updateUserProfile(id, request);
    }

    @GetMapping("/me")
    public UserProfileResponse getMe() {
        return userProfileService.getMe();
    }

    @GetMapping("/{id}")
    public UserProfileResponse getUserProfile(@PathVariable String id) {
        return userProfileService.getUserProfile(id);
    }

}
