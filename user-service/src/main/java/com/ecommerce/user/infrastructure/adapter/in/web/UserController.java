package com.ecommerce.user.infrastructure.adapter.in.web;

import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.port.in.GetUserUseCase;
import com.ecommerce.user.domain.port.in.UpdateProfileUseCase;
import com.ecommerce.user.infrastructure.adapter.in.web.dto.UpdateProfileRequest;
import com.ecommerce.user.infrastructure.adapter.in.web.dto.UserResponse;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestion des utilisateurs et profils")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final GetUserUseCase getUserUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

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


    @GetMapping("/internal/{id}/email")
    @Operation(summary = "Route interne pour récupérer l'email (Inter-services)", hidden = true)
    public ResponseEntity<InternalEmailResponse> getUserEmailInternal(@PathVariable("id") UUID id) {
        User user = getUserUseCase.getById(id);
        return ResponseEntity.ok(new InternalEmailResponse(user.getId(), user.getEmail()));
    }

    // Petit Record DTO interne pour formater la réponse JSON comme le Feign Client l'attend
    public record InternalEmailResponse(UUID id, String email) {}
}