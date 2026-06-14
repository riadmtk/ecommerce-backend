package com.ecommerce.user.infrastructure.adapter.in.web;

import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.port.in.GetUserUseCase;
import com.ecommerce.user.domain.port.in.UpdateProfileUseCase;
import com.ecommerce.user.domain.port.in.RequestPasswordResetUseCase;
import com.ecommerce.user.domain.port.in.ExecutePasswordResetUseCase;
import com.ecommerce.user.infrastructure.adapter.in.web.dto.UpdateProfileRequest;
import com.ecommerce.user.infrastructure.adapter.in.web.dto.UserResponse;
import com.ecommerce.user.infrastructure.adapter.out.persistence.UserJpaRepository;
import com.ecommerce.user.domain.model.UserRole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestion des utilisateurs et profils")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final GetUserUseCase getUserUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ExecutePasswordResetUseCase executePasswordResetUseCase;

    // Ajout du repository pour l'approche CQRS (modification directe)
    private final UserJpaRepository userJpaRepository;

    @GetMapping("/me")
    @Operation(summary = "Récupérer le profil de l'utilisateur connecté")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserUseCase.getByEmail(userDetails.getUsername());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PutMapping("/me")
    @Operation(summary = "Mettre à jour le profil de l'utilisateur connecté")
    public ResponseEntity<UserResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        User currentUser = getUserUseCase.getByEmail(userDetails.getUsername());
        User updatedUser = updateProfileUseCase.updateProfile(
                new UpdateProfileUseCase.UpdateProfileCommand(
                        currentUser.getId(),
                        request.firstName(),
                        request.lastName(),
                        request.phoneNumber()
                )
        );
        return ResponseEntity.ok(UserResponse.from(updatedUser));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupérer un utilisateur par son ID (admin uniquement)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        User user = getUserUseCase.getById(id);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupérer tous les utilisateurs (Admin uniquement)")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = getUserUseCase.getAllUsers();

        // On transforme la liste de modèles du domaine en liste de DTOs Web
        return ResponseEntity.ok(
                users.stream()
                        .map(UserResponse::from)
                        .toList()
        );
    }


    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier le rôle d'un utilisateur (Admin uniquement)")
    public ResponseEntity<Void> changeUserRole(
            @PathVariable UUID id,
            @RequestBody UpdateRoleRequest request) {

        try {
            // 1. Convert the String to your Domain Enum
            UserRole roleEnum = UserRole.valueOf(request.role().toUpperCase());

            // 2. Pass the Enum to the repository
            int updatedRows = userJpaRepository.updateRoleById(id, roleEnum);

            if (updatedRows == 0) {
                return ResponseEntity.notFound().build(); // L'utilisateur n'existe pas
            }

            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException e) {
            // The role string sent in the request doesn't match any value in UserRole Enum
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/internal/{id}/email")
    @Operation(summary = "Route interne pour récupérer l'email (Inter-services)", hidden = true)
    public ResponseEntity<InternalEmailResponse> getUserEmailInternal(@PathVariable("id") UUID id) {
        User user = getUserUseCase.getById(id);
        return ResponseEntity.ok(new InternalEmailResponse(user.getId(), user.getEmail()));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Demander une réinitialisation de mot de passe")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        requestPasswordResetUseCase.requestReset(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Exécuter la réinitialisation de mot de passe")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        executePasswordResetUseCase.executeReset(request.token(), request.newPassword());
        return ResponseEntity.ok().build();
    }

    // Petit Record DTO interne pour formater la réponse JSON comme le Feign Client l'attend
    public record InternalEmailResponse(UUID id, String email) {}
    public record ForgotPasswordRequest(String email) {}
    public record ResetPasswordRequest(String token, String newPassword) {}

    // NOUVEAU : DTO pour la mise à jour du rôle
    public record UpdateRoleRequest(String role) {}
}