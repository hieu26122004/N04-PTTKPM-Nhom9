package com.nhson.authservice.user;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT * FROM users WHERE username = :username";
//        String sql = "SELECT * FROM users WHERE user_id = :username";
        SqlParameterSource namedParameters = new MapSqlParameterSource("username", username);

        List<User> users = namedParameterJdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> {
            String userId = rs.getString("user_id");
            List<String> roles = getRolesByUserId(userId);
            List<String> permissions = getPermissionsByUserId(userId);
            return new User(
                    userId,
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    roles,
                    permissions,
                    rs.getString("status"),
                    rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                    rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null,
                    rs.getTimestamp("last_login") != null ? rs.getTimestamp("last_login").toLocalDateTime() : null,
                    rs.getBoolean("mfa_enabled"),
                    rs.getInt("token_version"),
                    rs.getString("provider"),
                    rs.getString("provider_id")
            );
        });

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return users.get(0);
    }

    public List<String> getRolesByUserId(String userId) {
        String sql = "SELECT role FROM user_roles WHERE user_id = :userId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);

        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, rowNum) ->
                rs.getString("role")
        );
    }
    public List<String> getPermissionsByUserId(String userId) {
        String sql = "SELECT permission FROM user_permissions WHERE user_id = :userId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);

        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, rowNum) ->
                rs.getString("permission")
        );
    }
}
