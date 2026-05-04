package com.ecommerce.user.domain.port.out;

import com.ecommerce.user.domain.model.User;

public interface UserEventPublisherPort {
    void publishUserRegistered(User user);
    void publishUserLoggedIn(User user);
}