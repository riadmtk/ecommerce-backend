package com.ecommerce.user.infrastructure.adapter.in.web;

import com.ecommerce.user.domain.exception.UserNotFoundException;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserRole;
import com.ecommerce.user.domain.port.in.GetUserUseCase;
import com.ecommerce.user.domain.port.in.UpdateProfileUseCase;
import com.ecommerce.user.domain.port.out.JwtPort;
import com.ecommerce.user.domain.port.out.UserRepositoryPort;
import com.ecommerce.user.infrastructure.config.JwtAuthenticationFilter;
import com.ecommerce.user.infrastructure.config.SecurityConfig;
import com.ecommerce.user.infrastructure.config.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = {KafkaAutoConfiguration.class}
)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, UserDetailsServiceImpl.class})
@DisplayName("UserController - Tests Infrastructure")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GetUserUseCase getUserUseCase;

    @MockitoBean
    private UpdateProfileUseCase updateProfileUseCase;

    @MockitoBean
    private JwtPort jwtPort;

    @MockitoBean
    private UserRepositoryPort userRepository;

    private User mockUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        mockUser = User.builder()
                .id(userId)
                .firstName("Mohammed Riad")
                .lastName("Moutaoukil")
                .email("riad@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        lenient().when(jwtPort.extractEmail(anyString())).thenReturn(null);
        lenient().when(jwtPort.isTokenValid(anyString(), anyString())).thenReturn(false);
    }

    @Test
    @WithMockUser(username = "riad@example.com")
    @DisplayName("GET /api/users/me - Doit retourner le profil de l'utilisateur connecté")
    void shouldReturnMyProfile() throws Exception {
        when(getUserUseCase.getByEmail("riad@example.com")).thenReturn(mockUser);

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("riad@example.com"))
                .andExpect(jsonPath("$.firstName").value("Mohammed Riad"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("GET /api/users/me - Doit retourner 401 sans JWT Token")
    void shouldReturn401WithoutJwt() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "riad@example.com")
    @DisplayName("PUT /api/users/me - Doit mettre à jour le profil")
    void shouldUpdateMyProfile() throws Exception {
        when(getUserUseCase.getByEmail("riad@example.com")).thenReturn(mockUser);
        when(updateProfileUseCase.updateProfile(any())).thenReturn(mockUser);

        String requestBody = """
                {
                    "firstName": "Riad",
                    "lastName": "Updated",
                    "phoneNumber": "+33612345678"
                }
                """;

        mockMvc.perform(put("/api/users/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/users/{id} - Doit retourner un utilisateur par ID")
    void shouldReturnUserById() throws Exception {
        when(getUserUseCase.getById(userId)).thenReturn(mockUser);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("riad@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/users/{id} - Doit retourner 404 si utilisateur introuvable")
    void shouldReturn404WhenUserNotFound() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(getUserUseCase.getById(unknownId))
                .thenThrow(new UserNotFoundException(unknownId));

        mockMvc.perform(get("/api/users/{id}", unknownId))
                .andExpect(status().isNotFound());
    }
}