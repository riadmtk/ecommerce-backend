package com.ecommerce.user.application.service;

import com.ecommerce.user.domain.exception.InvalidCredentialsException;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserRole;
import com.ecommerce.user.domain.port.in.LoginUseCase;
import com.ecommerce.user.domain.port.out.JwtPort;
import com.ecommerce.user.domain.port.out.PasswordEncoderPort;
import com.ecommerce.user.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService - Tests Application")
class LoginServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private JwtPort jwtPort;

    @InjectMocks
    private LoginService loginService;

    private User existingUser;
    private LoginUseCase.LoginCommand validCommand;

    @BeforeEach
    void setUp() {
        existingUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Mohammed Riad")
                .lastName("Moutaoukil")
                .email("riad@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        validCommand = new LoginUseCase.LoginCommand(
                "riad@example.com",
                "Password123!"
        );
    }

    @Test
    @DisplayName("Doit retourner un JWT Token si les credentials sont valides")
    void shouldReturnTokenWhenCredentialsAreValid() {
        when(userRepository.findByEmail("riad@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("Password123!", "encodedPassword"))
                .thenReturn(true);
        when(jwtPort.generateToken(existingUser))
                .thenReturn("jwt.token.here");
        when(jwtPort.getExpirationInSeconds())
                .thenReturn(86400L);

        LoginUseCase.LoginResult result = loginService.login(validCommand);

        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo("jwt.token.here");
        assertThat(result.tokenType()).isEqualTo("Bearer");
        assertThat(result.expiresIn()).isEqualTo(86400L);
    }

    @Test
    @DisplayName("Doit lever InvalidCredentialsException si email introuvable")
    void shouldThrowWhenEmailNotFound() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginService.login(validCommand))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtPort, never()).generateToken(any());
    }

    @Test
    @DisplayName("Doit lever InvalidCredentialsException si mot de passe incorrect")
    void shouldThrowWhenPasswordIsWrong() {
        when(userRepository.findByEmail("riad@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("Password123!", "encodedPassword"))
                .thenReturn(false);

        assertThatThrownBy(() -> loginService.login(validCommand))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtPort, never()).generateToken(any());
    }
}