package com.acnecare.acnecare_app_api.admin.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acnecare.acnecare_app_api.admin.dto.request.AdminPostCreationRequest;
import com.acnecare.acnecare_app_api.admin.dto.request.AdminPostUpdateRequest;
import com.acnecare.acnecare_app_api.admin.dto.response.AdminPostResponse;
import com.acnecare.acnecare_app_api.admin.dto.response.AdminUserResponse;
import com.acnecare.acnecare_app_api.admin.mapper.AdminMapper;
import com.acnecare.acnecare_app_api.common.exception.AppException;
import com.acnecare.acnecare_app_api.common.exception.ErrorCode;
import com.acnecare.acnecare_app_api.identity.entity.User;
import com.acnecare.acnecare_app_api.identity.repository.UserRepository;
import com.acnecare.acnecare_app_api.identity.service.UserService;
import com.acnecare.acnecare_app_api.post.service.PostService;
import com.acnecare.acnecare_app_api.profile.entity.UserProfile;
import com.acnecare.acnecare_app_api.profile.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminService {

    UserRepository userRepository;
    UserService userService;
    PostService postService;
    AdminMapper adminMapper;
    UserProfileRepository userProfileRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
                    return adminMapper.toAdminUserResponse(user, profile);
                })
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminUserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
        return adminMapper.toAdminUserResponse(user, profile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminUserResponse updateUserStatus(String id, String status) {
        userService.updateStatus(id, status);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
        return adminMapper.toAdminUserResponse(user, profile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<AdminPostResponse> getAllPosts() {
        return postService.getAllPosts();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminPostResponse getPostById(String id) {
        return postService.getPostById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminPostResponse createPost(AdminPostCreationRequest request) {
        return postService.createPost(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AdminPostResponse updatePost(String id, AdminPostUpdateRequest request) {
        return postService.updatePost(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deletePost(String id) {
        postService.deletePost(id);
    }
}
