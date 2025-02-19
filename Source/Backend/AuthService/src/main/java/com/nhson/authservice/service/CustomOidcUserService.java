package com.nhson.authservice.service;

import com.nhson.authservice.user.event.UserRegisterEvent;
import com.nhson.authservice.user.ProfileReq;
import com.nhson.authservice.user.User;
import com.nhson.authservice.user.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomOidcUserService extends OidcUserService {
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final GrantedAuthoritiesMapper grantedAuthoritiesMapper;
    public CustomOidcUserService(UserRepository userRepository, ApplicationEventPublisher eventPublisher, GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oidcUser.getAttribute("sub");
        OidcIdToken idToken = userRequest.getIdToken();
        OidcUserInfo userInfo = oidcUser.getUserInfo();
        User user = saveOrUpdateUser(providerId, registrationId, oidcUser);
        Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());
        user.getRoles().forEach(role -> {
            if(role.startsWith("ROLE_")) {
                String normalizedRole = role.replaceFirst("^ROLE_", "OIDC");
                authorities.add(new SimpleGrantedAuthority(normalizedRole));
            }
        });
        return new DefaultOidcUser(authorities, idToken, userInfo);
    }


    private User saveOrUpdateUser(String providerId, String provider, OidcUser oidcUser) {
        Optional<User> userOptional = userRepository.findByProviderIdAndProvider(providerId, provider);
        User user;

        if (userOptional.isEmpty()) {
            user = new User();
            user.setUserId(providerId);
            user.setProviderId(providerId);
            user.setProvider(provider);
            user.setEmail(oidcUser.getAttribute("email"));
            user.setUsername(providerId);
            user.setProvider(provider);
            user.setStatus("ACTIVE");
            user.setCreatedAt(LocalDateTime.now());
            user.setRoles(grantedAuthoritiesMapper.mapAuthorities(oidcUser.getAuthorities()).stream().map(role -> role.getAuthority()).collect(Collectors.toList()));
            userRepository.save(user);
            ProfileReq req = new ProfileReq(user);
            eventPublisher.publishEvent(new UserRegisterEvent(this,req));
        } else {
            user = userOptional.get();
            boolean isUpdated = false;

            String newEmail = oidcUser.getAttribute("email");
            if (newEmail != null && !newEmail.equals(user.getEmail())) {
                user.setEmail(newEmail);
                isUpdated = true;
            }
            if (isUpdated) {
                userRepository.update(user);
            }
        }

        return user;
    }
}

