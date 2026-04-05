package com.acnecare.acnecare_app_api.profile.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import com.acnecare.acnecare_app_api.profile.repository.UserProfileRepository;
import com.acnecare.acnecare_app_api.profile.dto.request.UserProfileCreationRequest;
import com.acnecare.acnecare_app_api.profile.dto.response.UserProfileResponse;
import com.acnecare.acnecare_app_api.profile.entity.UserProfile;
import com.acnecare.acnecare_app_api.profile.mapper.UserProfileMapper;
import com.acnecare.acnecare_app_api.common.exception.AppException;
import com.acnecare.acnecare_app_api.common.exception.ErrorCode;
import com.acnecare.acnecare_app_api.profile.dto.request.UserProfileUpdateRequest;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService {

    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;
    
    public UserProfileResponse createUserProfile(UserProfileCreationRequest request) {
        if (userProfileRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new AppException(ErrorCode.USER_PROFILE_ALREADY_EXISTS);
        }
        UserProfile userProfile = userProfileMapper.toUserProfile(request);
        userProfileRepository.save(userProfile);
        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public UserProfileResponse updateUserProfile(String userId, UserProfileUpdateRequest request) {
        UserProfile userProfile = userProfileRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));
        userProfileMapper.updateUserProfile(userProfile, request);
        userProfileRepository.save(userProfile);
        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public UserProfileResponse getMe() {
        UserProfile userProfile = userProfileRepository.findByUserId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));
        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public UserProfileResponse getUserProfile(String userId) {
        UserProfile userProfile = userProfileRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));
        return userProfileMapper.toUserProfileResponse(userProfile);
    }
}
