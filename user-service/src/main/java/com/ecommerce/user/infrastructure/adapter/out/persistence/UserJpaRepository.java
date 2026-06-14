package com.ecommerce.user.infrastructure.adapter.out.persistence;

import com.ecommerce.user.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.ecommerce.user.domain.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByResetToken(String resetToken);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.role = :newRole WHERE u.id = :userId")
    int updateRoleById(@Param("userId") UUID userId, @Param("newRole") UserRole newRole);
}