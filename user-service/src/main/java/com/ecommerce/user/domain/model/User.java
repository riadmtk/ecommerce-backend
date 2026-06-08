package com.ecommerce.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Random;
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

    private String resetToken;
    private LocalDateTime resetTokenExpiry;

    private boolean enabled;
    private String verificationCode;
    private LocalDateTime verificationCodeExpiry;

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
                .enabled(false)
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

    public void requestPasswordReset(String token, int expirationMinutes) {
        this.resetToken = token;
        this.resetTokenExpiry = LocalDateTime.now().plusMinutes(expirationMinutes);
        this.updatedAt = LocalDateTime.now();
    }

    public void resetPassword(String newEncodedPassword) {
        if (this.resetToken == null || this.resetTokenExpiry == null) {
            throw new IllegalStateException("No password reset was requested.");
        }
        if (LocalDateTime.now().isAfter(this.resetTokenExpiry)) {
            throw new IllegalStateException("Reset token has expired."); // You can create a specific Domain Exception for this!
        }

        this.password = newEncodedPassword;
        this.resetToken = null; // Invalidate token after use
        this.resetTokenExpiry = null;
        this.updatedAt = LocalDateTime.now();
    }

    // Ajouter une méthode pour générer code de vérification
    public void generateVerificationCode(int expiryMinutes) {
        this.verificationCode = String.format("%06d", new Random().nextInt(999999));
        this.verificationCodeExpiry = LocalDateTime.now().plusMinutes(expiryMinutes);
        this.updatedAt = LocalDateTime.now();
    }

    // Vérifier le code
    public boolean isVerificationCodeValid(String code) {
        return this.verificationCode != null
                && this.verificationCode.equals(code)
                && LocalDateTime.now().isBefore(this.verificationCodeExpiry);
    }

    // Activer le compte
    public void enable() {
        this.enabled = true;
        this.verificationCode = null;
        this.verificationCodeExpiry = null;
        this.updatedAt = LocalDateTime.now();
    }
}