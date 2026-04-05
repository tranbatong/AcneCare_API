package com.acnecare.acnecare_app_api.identity.dto.request;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequest {
    String email;
    String password;
    String firstName;
    String lastName;
    String phone;
    String address;
    double height;
    double weight;
    boolean gender;
}
