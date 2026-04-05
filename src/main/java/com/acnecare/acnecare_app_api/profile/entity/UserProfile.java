package com.acnecare.acnecare_app_api.profile.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String userId;
    String firstName;
    String lastName;
    String phone;
    String address;
    double height;
    double weight;
    boolean gender;

}
