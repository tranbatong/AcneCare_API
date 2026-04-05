package com.acnecare.acnecare_app_api.common.exception;

import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
        EMAIL_ALREADY_EXISTS(1001, "Email already exists", HttpStatus.BAD_REQUEST),
        USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
        INVALID_KEY(1003, "Invalid key", HttpStatus.BAD_REQUEST),
        ACCESS_DENIED(1004, "Access denied", HttpStatus.FORBIDDEN),
        UNAUTHORIZED(1005, "Unauthorized", HttpStatus.UNAUTHORIZED),
        FORBIDDEN(1006, "Forbidden", HttpStatus.FORBIDDEN),
        NOT_FOUND(1007, "Not found", HttpStatus.NOT_FOUND),
        BAD_REQUEST(1008, "Bad request", HttpStatus.BAD_REQUEST),
        INTERNAL_SERVER_ERROR(1009, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
        SERVICE_UNAVAILABLE(1010, "Service unavailable", HttpStatus.SERVICE_UNAVAILABLE),
        USER_IS_BLOCKED(1011, "User is blocked", HttpStatus.FORBIDDEN),
        INVALID_CREDENTIALS(1012, "Invalid credentials", HttpStatus.UNAUTHORIZED),
        INVALID_TOKEN(1013, "Invalid token", HttpStatus.UNAUTHORIZED),
        UNAUTHENTICATED(1014, "Unauthenticated", HttpStatus.UNAUTHORIZED),
        ROLE_NOT_FOUND(1015, "Role not found", HttpStatus.NOT_FOUND),

        POST_NOT_FOUND(1018, "Post not found", HttpStatus.NOT_FOUND),
        COMMENT_NOT_FOUND(1019, "Comment not found", HttpStatus.NOT_FOUND),
        LIKE_NOT_FOUND(1020, "Like not found", HttpStatus.NOT_FOUND),
        POST_IMAGE_NOT_FOUND(1021, "Post image not found", HttpStatus.NOT_FOUND),

        ACNE_NOT_FOUND(1016, "Acne not found", HttpStatus.NOT_FOUND),
        ACNE_PREDICTION_NOT_FOUND(1017, "Acne prediction not found", HttpStatus.NOT_FOUND),

        USER_PROFILE_NOT_FOUND(1018, "User profile not found", HttpStatus.NOT_FOUND),
        USER_PROFILE_ALREADY_EXISTS(1019, "User profile already exists", HttpStatus.BAD_REQUEST),
        UNCATEGORIZED_ERROR(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR);

        int code;
        String message;
        HttpStatusCode statusCode;
}