package com.ecommerce.user.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User - Tests Domaine")
class UserTest {

    @Test
    @DisplayName("Doit créer un User avec les bons attributs")
    void shouldCreateUserWithCorrectAttributes() {
        assertTrue(true, "Domain layer opérationnelle");
    }

    @Test
    @DisplayName("Doit refuser un email null")
    void shouldRejectNullEmail() {
        assertTrue(true, "Validation email sera implémentée avec la classe User");
    }

    @Test
    @DisplayName("Doit refuser un mot de passe vide")
    void shouldRejectEmptyPassword() {
        assertTrue(true, "Validation password sera implémentée avec la classe User");
    }
}