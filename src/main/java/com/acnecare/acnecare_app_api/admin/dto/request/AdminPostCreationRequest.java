package com.acnecare.acnecare_app_api.admin.dto.request;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPostCreationRequest {
    String title;
    String content;
    String authorId;
    String status;
}
