package com.acnecare.acnecare_app_api.identity.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.acnecare.acnecare_app_api.identity.service.UserService;
import com.acnecare.acnecare_app_api.identity.dto.request.UserCreationRequest;
import com.acnecare.acnecare_app_api.identity.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.acnecare.acnecare_app_api.identity.dto.request.UserUpdatePasswordRequest;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    public UserResponse createUser(@RequestBody UserCreationRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/me")
    public UserResponse getMe() {
        return userService.getMe();
    }

    @PutMapping("/password/{id}")
    public UserResponse updatePassword(@PathVariable String id, @RequestBody UserUpdatePasswordRequest request) {
        return userService.updatePassword(id, request);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

}
