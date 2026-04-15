package com.ecommerce.user.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("User - Tests Domaine")
class UserTest {

    @Test
    @DisplayName("Doit créer un User avec statut USER par défaut")
    void shouldCreateUserWithDefaultRoleUser() {
        User user = User.create(
                "Mohammed Riad",
                "Moutaoukil",
                "riad@example.com",
                "encodedPassword"
        );

        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("Mohammed Riad");
        assertThat(user.getLastName()).isEqualTo("Moutaoukil");
        assertThat(user.getEmail()).isEqualTo("riad@example.com");
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Doit générer un UUID unique pour chaque User créé")
    void shouldGenerateUniqueIdForEachUser() {
        User user1 = User.create("Riad", "M", "riad@example.com", "pwd");
        User user2 = User.create("Amine", "K", "amine@example.com", "pwd");

        assertThat(user1.getId()).isNotEqualTo(user2.getId());
    }

    @Test
    @DisplayName("Doit mettre à jour le profil correctement")
    void shouldUpdateProfileCorrectly() {
        User user = User.create("Riad", "M", "riad@example.com", "pwd");

        User updated = user.updateProfile("NouveauPrenom", "NouveauNom", "+33612345678");

        assertThat(updated.getFirstName()).isEqualTo("NouveauPrenom");
        assertThat(updated.getLastName()).isEqualTo("NouveauNom");
        assertThat(updated.getPhoneNumber()).isEqualTo("+33612345678");
        assertThat(updated.getEmail()).isEqualTo("riad@example.com");
        assertThat(updated.getPassword()).isEqualTo("pwd");
        assertThat(updated.getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("Doit conserver les anciens champs si null dans updateProfile")
    void shouldKeepExistingFieldsWhenUpdateWithNull() {
        User user = User.create("Riad", "Moutaoukil", "riad@example.com", "pwd");

        User updated = user.updateProfile(null, null, "+33612345678");

        assertThat(updated.getFirstName()).isEqualTo("Riad");
        assertThat(updated.getLastName()).isEqualTo("Moutaoukil");
        assertThat(updated.getPhoneNumber()).isEqualTo("+33612345678");
    }

    @Test
    @DisplayName("Doit mettre à jour updatedAt lors d'un updateProfile")
    void shouldUpdateTimestampOnProfileUpdate() throws InterruptedException {
        User user = User.create("Riad", "M", "riad@example.com", "pwd");
        Thread.sleep(10);

        User updated = user.updateProfile("Nouveau", "Nom", null);

        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(user.getCreatedAt());
    }
}