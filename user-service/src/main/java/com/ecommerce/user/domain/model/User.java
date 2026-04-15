package com.ecommerce.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static User create(String firstName, String lastName,
                              String email, String encodedPassword) {
        return User.builder()
                .id(UUID.randomUUID())   // ← génération systématique
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(encodedPassword)
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public User updateProfile(String firstName, String lastName, String phoneNumber) {
        return User.builder()
                .id(this.id)
                .firstName(firstName != null ? firstName : this.firstName)
                .lastName(lastName != null ? lastName : this.lastName)
                .email(this.email)
                .password(this.password)
                .phoneNumber(phoneNumber != null ? phoneNumber : this.phoneNumber)
                .role(this.role)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}