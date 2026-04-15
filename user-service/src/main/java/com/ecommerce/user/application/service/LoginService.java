package com.ecommerce.user.application.service;

import com.ecommerce.user.domain.exception.InvalidCredentialsException;
import com.ecommerce.user.domain.model.User;
import com.ecommerce.user.domain.port.in.LoginUseCase;
import com.ecommerce.user.domain.port.out.JwtPort;
import com.ecommerce.user.domain.port.out.PasswordEncoderPort;
import com.ecommerce.user.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtPort jwtPort;

    @Override
    public LoginResult login(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtPort.generateToken(user);

        return new LoginResult(
                token,
                "Bearer",
                jwtPort.getExpirationInSeconds()
        );
    }
}