package com.ecommerce.user.domain.port.in;

import com.ecommerce.user.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface GetUserUseCase {
    User getById(UUID id);
    User getByEmail(String email);
    List<User> getAllUsers();
}