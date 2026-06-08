package com.ecommerce.user.infrastructure.adapter.in.web;

import com.ecommerce.user.domain.exception.InvalidVerificationCodeException;
import com.ecommerce.user.domain.exception.UserNotFoundException;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.port.in.GetUserUseCase;
import com.ecommerce.user.domain.port.in.LoginUseCase;
import com.ecommerce.user.domain.port.in.RegisterUseCase;
import com.ecommerce.user.domain.port.out.JwtPort;
import com.ecommerce.user.domain.port.out.PasswordEncoderPort;
import com.ecommerce.user.domain.port.out.UserEventPublisherPort;
import com.ecommerce.user.domain.port.out.UserRepositoryPort;
import com.ecommerce.user.infrastructure.adapter.in.web.dto.*;
import com.ecommerce.user.infrastructure.adapter.out.feign.NotificationServiceClient;
import com.ecommerce.user.infrastructure.adapter.out.feign.dto.VerificationCodeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentification et génération de JWT Token")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final JwtPort jwtPort;   // ← Ajout du port JWT
    private final UserEventPublisherPort userEventPublisherPort;
    private final GetUserUseCase getUserUseCase;

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    // NOUVEAU : Injection de l'événement Spring
    private final ApplicationEventPublisher applicationEventPublisher;

    // NOUVEAU : Injection du client Feign pour notification-service
    private final NotificationServiceClient notificationClient;

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = registerUseCase.register(new RegisterUseCase.RegisterCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password()
        ));

        // Génération directe du token
        String token = jwtPort.generateToken(user);
        LoginUseCase.LoginResult loginResult = new LoginUseCase.LoginResult(
                token,
                "Bearer",
                jwtPort.getExpirationInSeconds()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(
                loginResult.token(),
                loginResult.tokenType(),
                loginResult.expiresIn(),
                UserResponse.from(user)
        ));
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion d'un utilisateur existant")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginUseCase.LoginResult result = loginUseCase.login(
                new LoginUseCase.LoginCommand(request.email(), request.password())
        );

        // 2. Récupération de l'utilisateur pour l'événement
        User user = getUserUseCase.getByEmail(request.email());

        // 3. Publication de l'événement "UserLoggedIn"
        userEventPublisherPort.publishUserLoggedIn(user);


        return ResponseEntity.ok(new AuthResponse(
                result.token(),
                result.tokenType(),
                result.expiresIn(),
                null
        ));
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestBody VerifyRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        if (user.isVerificationCodeValid(request.code())) {
            user.enable();
            userRepository.save(user);

            // 🔔 Publier l'événement pour que notification-service envoie l'email de bienvenue
            applicationEventPublisher.publishEvent(user);

            return ResponseEntity.ok().build();
        } else {
            throw new InvalidVerificationCodeException();
        }
    }

    @PostMapping("/resend-code")
    public ResponseEntity<Void> resendVerificationCode(@RequestBody ResendCodeRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        if (user.isEnabled()) {
            throw new IllegalStateException("Email already verified");
        }

        user.generateVerificationCode(15);
        userRepository.save(user);

        // Envoyer le nouveau code via notification-service
        notificationClient.sendVerificationCode(
                new VerificationCodeRequest(user.getEmail(), user.getVerificationCode())
        );

        return ResponseEntity.ok().build();
    }
}