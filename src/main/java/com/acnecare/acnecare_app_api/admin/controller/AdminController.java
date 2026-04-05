package com.acnecare.acnecare_app_api.admin.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.acnecare.acnecare_app_api.admin.service.AdminService;
import com.acnecare.acnecare_app_api.admin.dto.request.AdminUserStatusUpdateRequest;
import com.acnecare.acnecare_app_api.admin.dto.request.AdminPostCreationRequest;
import com.acnecare.acnecare_app_api.admin.dto.request.AdminPostUpdateRequest;
import com.acnecare.acnecare_app_api.admin.dto.response.AdminUserResponse;
import com.acnecare.acnecare_app_api.admin.dto.response.AdminPostResponse;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {

    AdminService adminService;

    @GetMapping("/users")
    public List<AdminUserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public AdminUserResponse getUserById(@PathVariable String id) {
        return adminService.getUserById(id);
    }

    @PutMapping("/users/{id}/status")
    public AdminUserResponse updateUserStatus(
            @PathVariable String id,
            @RequestBody AdminUserStatusUpdateRequest request) {
        return adminService.updateUserStatus(id, request.getStatus());
    }

    @GetMapping("/posts")
    public List<AdminPostResponse> getAllPosts() {
        return adminService.getAllPosts();
    }

    @GetMapping("/posts/{id}")
    public AdminPostResponse getPostById(@PathVariable String id) {
        return adminService.getPostById(id);
    }

    @PostMapping("/posts")
    public AdminPostResponse createPost(@RequestBody AdminPostCreationRequest request) {
        return adminService.createPost(request);
    }

    @PutMapping("/posts/{id}")
    public AdminPostResponse updatePost(
            @PathVariable String id,
            @RequestBody AdminPostUpdateRequest request) {
        return adminService.updatePost(id, request);
    }

    @DeleteMapping("/posts/{id}")
    public void deletePost(@PathVariable String id) {
        adminService.deletePost(id);
    }
}
