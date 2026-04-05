package com.acnecare.acnecare_app_api.profile.dto.request;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileUpdateRequest {

    String firstName;
    String lastName;
    String phone;
    String address;
    double height;
    double weight;
    boolean gender;
}
