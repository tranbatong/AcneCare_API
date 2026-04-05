package com.acnecare.acnecare_app_api.profile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import com.acnecare.acnecare_app_api.profile.dto.request.UserProfileCreationRequest;
import com.acnecare.acnecare_app_api.profile.dto.response.UserProfileResponse;
import com.acnecare.acnecare_app_api.profile.entity.UserProfile;
import com.acnecare.acnecare_app_api.profile.dto.request.UserProfileUpdateRequest;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfile toUserProfile(UserProfileCreationRequest request);
    UserProfileResponse toUserProfileResponse(UserProfile userProfile);
    void updateUserProfile(@MappingTarget UserProfile userProfile, UserProfileUpdateRequest request);
}
