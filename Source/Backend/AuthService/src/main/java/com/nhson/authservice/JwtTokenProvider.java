package com.nhson.authservice;

import com.nhson.authservice.service.TokenBlacklistService;
import com.nhson.authservice.user.User;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static String SECRET_KEY;
    private final GrantedAuthoritiesMapper grantedAuthoritiesMapper;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.expiration:86400000}")
    private long expirationTime;

    public JwtTokenProvider(GrantedAuthoritiesMapper grantedAuthoritiesMapper, TokenBlacklistService tokenBlacklistService) {
        this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostConstruct
    private void init() {
        SECRET_KEY = System.getenv("SIGNING_KEY");
        if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
            SECRET_KEY = loadSecretKeyFromFile();
        }
    }

    private static String loadSecretKeyFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("secret.key"))) {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load secret key from file", e);
        }
    }

    public String generateToken(User user) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiryDate = new Date(nowMillis + expirationTime);

        List<String> scopes = user.getRoles().stream()
                .map(role -> role.replaceFirst("ROLE_", ""))
                .collect(Collectors.toList());
        Map<String, Object> claims = new HashMap<>();
        claims.put("scope", scopes);
        claims.put("email", user.getEmail());
        claims.put("username", user.getUsername());

        return Jwts.builder()
                .setSubject(user.getUserId())
                .setIssuedAt(now)
                .setNotBefore(now)
                .setExpiration(expiryDate)
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String generateToken(OAuth2User oAuth2User) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiryDate = new Date(nowMillis + expirationTime);
        Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();

        // Phân loại scopes và permissions
        List<String> scopes = authorities.stream()
                .filter(authority -> !authority.getAuthority().startsWith("PERMISSION_"))
                .map(this::mappedRolePermission)
                .collect(Collectors.toList());

        // Xây dựng JWT
        return Jwts.builder()
                .setSubject(oAuth2User.getName()) // Sử dụng tên người dùng làm subject
                .setIssuedAt(now)                 // Ngày phát hành
                .setExpiration(expiryDate)        // Ngày hết hạn
                .claim("scope", scopes)           // Đặt danh sách scopes
                .claim("email", oAuth2User.getAttribute("email")) // Email từ OAuth2User
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)   // Ký với HS256
                .compact();
    }


    public Claims validateToken(String token) {
        if(tokenBlacklistService.isTokenRevoked(token)){
            throw new IllegalArgumentException("Token has been revoked");
        }
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            System.err.println("Token expired: " + e.getMessage());
            return null;
        } catch (UnsupportedJwtException e) {
            System.err.println("Unsupported token: " + e.getMessage());
            return null;
        } catch (MalformedJwtException e) {
            System.err.println("Malformed token: " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal argument token: " + e.getMessage());
            return null;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    public boolean isTokenExpired(String token) {
        Claims claims = validateToken(token);
        return claims != null && claims.getExpiration().before(new Date());
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        List<String> scopes = validateToken(token).get("scope", List.class);
        return scopes.stream()
                .map(scope -> new SimpleGrantedAuthority("ROLE_" + scope))
                .collect(Collectors.toList());
    }

    public void revokeToken(String token){
        tokenBlacklistService.addToBlacklist(token, expirationTime);
    }

    private String mappedRolePermission(GrantedAuthority authority) {
        String roleName = authority.getAuthority();
        if (authority instanceof OidcUserAuthority) {
            if (roleName.startsWith("OIDC_")) {
                roleName = roleName.substring(5);
            }
        }
        else if (roleName.startsWith("SCOPE_")) {
            roleName = roleName.substring(6);
            if (roleName.startsWith("https://www.googleapis.com/auth/")) {
                roleName = roleName.replace("https://www.googleapis.com/auth/", "googleapis.auth.");
            }
        } else if (roleName.startsWith("PERMISSION_")) {
            roleName = roleName.substring(11);
        }
        return roleName;
    }


}
