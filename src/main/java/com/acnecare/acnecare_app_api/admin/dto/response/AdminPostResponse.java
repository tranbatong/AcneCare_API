package com.acnecare.acnecare_app_api.admin.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPostResponse {
    String id;
    String title;
    String content;
    String authorId;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
