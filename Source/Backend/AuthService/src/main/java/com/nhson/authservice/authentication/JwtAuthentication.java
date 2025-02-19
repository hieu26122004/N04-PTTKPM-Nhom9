package com.nhson.authservice.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class JwtAuthentication implements Authentication {
    private Object principal;
    private Object credentials;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean isAuthenticated = false;
    public JwtAuthentication(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null) {
            this.authorities = AuthorityUtils.NO_AUTHORITIES;
        } else {
            Iterator var2 = authorities.iterator();

            while (var2.hasNext()) {
                GrantedAuthority a = (GrantedAuthority) var2.next();
                Assert.notNull(a, "Authorities collection cannot contain any null elements");
            }
            this.authorities = Collections.unmodifiableList(new ArrayList(authorities));
        }
    }

    private JwtAuthentication(Object principal, Object credentials) {
        this.principal = principal;
        this.credentials = credentials;
    }

    public JwtAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        this.principal = principal;
        this.credentials = credentials;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return (String) principal;
    }

    public static JwtAuthentication unauthenticated(Object principal, Object credentials) {
        return new JwtAuthentication(principal, credentials);
    }

    public static JwtAuthentication authenticated(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        JwtAuthentication authenticated = new JwtAuthentication(principal, credentials, authorities);
        authenticated.setAuthenticated(true);
        return authenticated;
    }
}
