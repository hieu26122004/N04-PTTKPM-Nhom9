package com.nhson.authservice.authentication;

import com.nhson.authservice.user.User;
import com.nhson.authservice.user.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final UserRepository userRepository;
    public JwtAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String usernameReq = authentication.getName();
        User user = userRepository.findById(usernameReq);
        if (user.getUserId().equals(usernameReq)) {
            return JwtAuthentication.authenticated(
                    usernameReq,
                    "",
                    user.getAuthorities()
            );
        } else {
            throw new BadCredentialsException("Not found any user with username: " + authentication.getName());
        }
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.isAssignableFrom(authentication);
    }
}
