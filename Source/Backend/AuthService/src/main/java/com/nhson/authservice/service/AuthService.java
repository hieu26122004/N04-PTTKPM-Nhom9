package com.nhson.authservice.service;

import com.nhson.authservice.AppConfig;
import com.nhson.authservice.JwtTokenProvider;
import com.nhson.authservice.email.publisher.MagicLinkGeneratedApplicationPublisher;
import com.nhson.authservice.email.publisher.UpdatePasswordApplicationPublisher;
import com.nhson.authservice.user.event.UserRegisterEvent;
import com.nhson.authservice.user.ProfileReq;
import com.nhson.authservice.user.User;
import com.nhson.authservice.user.UserRepository;
import com.nhson.authservice.user.UserToken;
import com.nhson.authservice.user.publisher.UserLoginPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final SnowflakeIdService snowflakeIdService = new SnowflakeIdService();
    private final ApplicationEventPublisher eventPublisher;
    private final MagicLinkGeneratedApplicationPublisher magicLinkGeneratedApplicationPublisher;
    private final UserLoginPublisher userLoginPublisher;
    private final UpdatePasswordApplicationPublisher updatePasswordApplicationPublisher;
    private final AppConfig appConfig;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, ApplicationEventPublisher eventPublisher, MagicLinkGeneratedApplicationPublisher magicLinkGeneratedApplicationPublisher, UserLoginPublisher userLoginPublisher, UpdatePasswordApplicationPublisher updatePasswordApplicationPublisher, AppConfig appConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.eventPublisher = eventPublisher;
        this.magicLinkGeneratedApplicationPublisher = magicLinkGeneratedApplicationPublisher;
        this.userLoginPublisher = userLoginPublisher;
        this.updatePasswordApplicationPublisher = updatePasswordApplicationPublisher;
        this.appConfig = appConfig;
    }

    @Transactional
    public String register(String username, String password, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        ArrayList<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        ArrayList<String> permissions = new ArrayList<>();

        User user = new User(String.valueOf(snowflakeIdService.generateId()), username, password, email, roles, permissions,
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                1,
                null,
                null);
        userRepository.save(user);

        ProfileReq req = new ProfileReq(user);
        eventPublisher.publishEvent(new UserRegisterEvent(this, req));
        return jwtTokenProvider.generateToken(user);
    }

    public String login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        User user = (User) authentication.getPrincipal();
        userLoginPublisher.publishEvent(user);
        return jwtTokenProvider.generateToken(user);
    }

    public String generateMagicLink(User user) {
        String token = UUID.randomUUID().toString();
        long expiryTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.toMillis(appConfig.getPwTokenTtl());
        UserToken userToken = new UserToken(user.getUserId(), user.getUsername(), user.getEmail(), token, expiryTime);
        userRepository.saveToken(userToken);
        String magicLink = "http://localhost:8080/auth/reset-password?token=" + token + "&userId=" + user.getUserId();
        magicLinkGeneratedApplicationPublisher.publish(
                magicLink,
                Collections.singletonList(user.getEmail()),
                user.getUsername()
        );
        return magicLink;
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getUserId());
        params.put("password", user.getPassword());
        userRepository.update(params);

        updatePasswordApplicationPublisher.publish(newPassword, Collections.singletonList(user.getEmail()), user.getUsername());
        userRepository.deleteTokenByUserId(user.getUserId());
    }
}
