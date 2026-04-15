package com.ecommerce.user.infrastructure.adapter.in.web;

import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.model.UserRole;
import com.ecommerce.user.domain.port.in.LoginUseCase;
import com.ecommerce.user.domain.port.in.RegisterUseCase;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeAutoConfiguration = {KafkaAutoConfiguration.class}
)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, UserDetailsServiceImpl.class})
@DisplayName("AuthController - Tests Infrastructure")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterUseCase registerUseCase;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @MockitoBean
    private JwtPort jwtPort;   // ← mock requis pour la nouvelle implémentation

    @MockitoBean
    private UserRepositoryPort userRepository;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Mohammed Riad")
                .lastName("Moutaoukil")
                .email("riad@example.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Configurations par défaut pour éviter les interférences
        lenient().when(jwtPort.extractEmail(anyString())).thenReturn(null);
        lenient().when(jwtPort.isTokenValid(anyString(), anyString())).thenReturn(false);
        lenient().when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("POST /api/auth/register - Doit créer un compte et retourner 201")
    void shouldRegisterAndReturn201() throws Exception {
        // Mock de l'enregistrement
        when(registerUseCase.register(any())).thenReturn(mockUser);

        // Mock de la génération de token par JwtPort
        when(jwtPort.generateToken(any(User.class))).thenReturn("jwt.token");
        when(jwtPort.getExpirationInSeconds()).thenReturn(86400L);

        String requestBody = """
                {
                    "firstName": "Mohammed Riad",
                    "lastName": "Moutaoukil",
                    "email": "riad@example.com",
                    "password": "Password123!"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(86400L));
    }

    @Test
    @DisplayName("POST /api/auth/register - Doit retourner 400 si données invalides")
    void shouldReturn400WhenDataInvalid() throws Exception {
        String requestBody = """
                {
                    "firstName": "",
                    "lastName": "",
                    "email": "email-invalide",
                    "password": "123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - Doit retourner 200 avec JWT Token")
    void shouldLoginAndReturnToken() throws Exception {
        when(loginUseCase.login(any())).thenReturn(
                new LoginUseCase.LoginResult("jwt.token", "Bearer", 86400L)
        );

        String requestBody = """
                {
                    "email": "riad@example.com",
                    "password": "Password123!"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Doit retourner 400 si email invalide")
    void shouldReturn400WhenEmailInvalid() throws Exception {
        String requestBody = """
                {
                    "email": "email-invalide",
                    "password": "Password123!"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}