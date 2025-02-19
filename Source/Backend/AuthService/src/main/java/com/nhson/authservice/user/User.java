package com.nhson.authservice.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    protected String userId;
    protected String username;
    protected String password;
    protected String email;
    protected List<String> roles;
    protected List<String> permissions;
    protected String status;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected LocalDateTime lastLogin;
    protected boolean mfaEnabled;
    protected int tokenVersion;
    protected String provider;
    protected String providerId;



    public User(String userId, String username, String password, String email, List<String> roles,
                List<String> permissions, String status, LocalDateTime createdAt, LocalDateTime updatedAt,
                LocalDateTime lastLogin, boolean mfaEnabled, int tokenVersion, String provider, String providerId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLogin = lastLogin;
        this.mfaEnabled = mfaEnabled;
        this.tokenVersion = tokenVersion;
        this.provider = provider;
        this.providerId = providerId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> roleAuthorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        return roleAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equalsIgnoreCase(status);
    }
}
