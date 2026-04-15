package com.ecommerce.user.application.service;

import com.ecommerce.user.domain.exception.EmailAlreadyExistsException;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserRole;
import com.ecommerce.user.domain.port.in.RegisterUseCase;
import com.ecommerce.user.domain.port.out.PasswordEncoderPort;
import com.ecommerce.user.domain.port.out.UserEventPublisherPort;
import com.ecommerce.user.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterService - Tests Application")
class RegisterServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private UserEventPublisherPort eventPublisher; // ← ajouter

    @InjectMocks
    private RegisterService registerService;

    private RegisterUseCase.RegisterCommand validCommand;

    @BeforeEach
    void setUp() {
        validCommand = new RegisterUseCase.RegisterCommand(
                "Mohammed Riad",
                "Moutaoukil",
                "riad@example.com",
                "Password123!"
        );
    }

    @Test
    @DisplayName("Doit créer un utilisateur avec succès")
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = registerService.register(validCommand);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("riad@example.com");
        assertThat(result.getFirstName()).isEqualTo("Mohammed Riad");
        assertThat(result.getRole()).isEqualTo(UserRole.USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Doit lever EmailAlreadyExistsException si email déjà utilisé")
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("riad@example.com")).thenReturn(true);

        assertThatThrownBy(() -> registerService.register(validCommand))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("riad@example.com");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Doit encoder le mot de passe avant de sauvegarder")
    void shouldEncodePasswordBeforeSaving() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        registerService.register(validCommand);

        verify(passwordEncoder).encode("Password123!");
    }

    @Test
    @DisplayName("Doit publier un event UserRegistered après inscription")
    void shouldPublishUserRegisteredEvent() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        registerService.register(validCommand);

        verify(eventPublisher).publishUserRegistered(any(User.class)); // ← vérifier
    }
}