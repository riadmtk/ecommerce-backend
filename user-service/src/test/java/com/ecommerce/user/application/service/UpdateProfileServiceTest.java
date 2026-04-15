package com.ecommerce.user.application.service;

import com.ecommerce.user.domain.exception.UserNotFoundException;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserRole;
import com.ecommerce.user.domain.port.in.UpdateProfileUseCase;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProfileService - Tests Application")
class UpdateProfileServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private UpdateProfileService updateProfileService;

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
    @DisplayName("Doit mettre à jour le profil avec succès")
    void shouldUpdateProfileSuccessfully() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        UpdateProfileUseCase.UpdateProfileCommand command =
                new UpdateProfileUseCase.UpdateProfileCommand(
                        userId,
                        "Riad",
                        "Moutaoukil Updated",
                        "+33612345678"
                );

        User result = updateProfileService.updateProfile(command);

        assertThat(result.getFirstName()).isEqualTo("Riad");
        assertThat(result.getLastName()).isEqualTo("Moutaoukil Updated");
        assertThat(result.getPhoneNumber()).isEqualTo("+33612345678");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Doit conserver les champs non modifiés si null")
    void shouldKeepExistingFieldsWhenNull() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));

        UpdateProfileUseCase.UpdateProfileCommand command =
                new UpdateProfileUseCase.UpdateProfileCommand(
                        userId, null, null, "+33612345678"
                );

        User result = updateProfileService.updateProfile(command);

        assertThat(result.getFirstName()).isEqualTo("Mohammed Riad");
        assertThat(result.getLastName()).isEqualTo("Moutaoukil");
        assertThat(result.getPhoneNumber()).isEqualTo("+33612345678");
    }

    @Test
    @DisplayName("Doit lever UserNotFoundException si utilisateur introuvable")
    void shouldThrowWhenUserNotFound() {
        UUID unknownId = UUID.randomUUID();
        when(userRepository.findById(unknownId))
                .thenReturn(Optional.empty());

        UpdateProfileUseCase.UpdateProfileCommand command =
                new UpdateProfileUseCase.UpdateProfileCommand(
                        unknownId, "Riad", "M", null
                );

        assertThatThrownBy(() -> updateProfileService.updateProfile(command))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any());
    }
}