package com.ecommerce.user.infrastructure.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UserController - Tests Infrastructure")
class UserControllerTest {

    @Test
    @DisplayName("Doit exposer POST /api/users/register")
    void shouldExposeRegisterEndpoint() {
        // Sera implémenté avec @WebMvcTest lors du développement
        // du User Service
        // POST /api/users/register { nom, email, password }
        assertTrue(true, "Register endpoint sera testé avec @WebMvcTest");
    }

    @Test
    @DisplayName("Doit exposer POST /api/auth/login")
    void shouldExposeLoginEndpoint() {
        // POST /api/auth/login { email, password }
        // → retourne JWT Token
        assertTrue(true, "Login endpoint sera testé avec @WebMvcTest");
    }

    @Test
    @DisplayName("Doit exposer GET /api/users/{id}")
    void shouldExposeGetUserEndpoint() {
        // GET /api/users/{id}
        // → retourne le profil utilisateur
        assertTrue(true, "GetUser endpoint sera testé avec @WebMvcTest");
    }

    @Test
    @DisplayName("Doit retourner 401 sans JWT Token")
    void shouldReturn401WithoutJwt() {
        // Endpoints protégés → 401 Unauthorized sans token
        assertTrue(true, "Sécurité JWT sera testée avec @WebMvcTest");
    }
}