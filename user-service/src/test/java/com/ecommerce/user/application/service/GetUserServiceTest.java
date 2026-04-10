package com.ecommerce.user.application.service;

import com.ecommerce.user.domain.exception.UserNotFoundException;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserRole;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserService - Tests Application")
class GetUserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private GetUserService getUserService;

    private User existingUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        existingUser = User.builder()
                .id(userId)
                .firstName("Mohammed Riad")
                .lastName("Moutaoukil")
                .email("riad@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Doit retourner un utilisateur par son ID")
    void shouldReturnUserById() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        User result = getUserService.getById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getEmail()).isEqualTo("riad@example.com");
    }

    @Test
    @DisplayName("Doit lever UserNotFoundException si ID introuvable")
    void shouldThrowWhenUserIdNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(userRepository.findById(unknownId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> getUserService.getById(unknownId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Doit retourner un utilisateur par son email")
    void shouldReturnUserByEmail() {
        when(userRepository.findByEmail("riad@example.com"))
                .thenReturn(Optional.of(existingUser));

        User result = getUserService.getByEmail("riad@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("riad@example.com");
    }

    @Test
    @DisplayName("Doit lever UserNotFoundException si email introuvable")
    void shouldThrowWhenEmailNotFound() {
        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> getUserService.getByEmail("unknown@example.com"))
                .isInstanceOf(UserNotFoundException.class);
    }
}