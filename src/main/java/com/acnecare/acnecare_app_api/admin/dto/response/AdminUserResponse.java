package com.acnecare.acnecare_app_api.admin.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
    String id;
    String email;
    String status;
    String firstName;
    String lastName;
    String phone;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime lastLoginAt;
    List<String> roles;
}
