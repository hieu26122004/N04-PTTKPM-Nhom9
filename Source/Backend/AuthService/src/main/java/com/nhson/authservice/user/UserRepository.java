package com.nhson.authservice.user;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public class UserRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /**
     * Tìm kiếm người dùng theo username, dùng cho tính năng đăng ký , đảm bảo username không trùng nhau
     * @param username
     * @return boolean
     * */
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource("username", username);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    /**
     * Tìm kiếm người dùng theo email, dùng cho tính năng quên mật khẩu
     * @param email
     * @return boolean
     * */
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource("email", email);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource("email", email);

        return namedParameterJdbcTemplate.query(sql, params, rs -> {
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getString("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                return Optional.of(user);
            }
            return Optional.empty();
        });
    }

    /**
     * Lưu toàn bộ thông tin của User, sử dụng để đăng ký người dùng ,
     * @param user
     * */
    @Transactional
    public void save(User user) {
        String userSql = "INSERT INTO users (" +
                "user_id, username, password, email, status, provider, provider_id, last_login, mfa_enabled, token_version" +
                ") VALUES (" +
                ":userId, :username, :password, :email, :status, :provider, :providerId, :lastLogin, :mfaEnabled, :tokenVersion" +
                ")";

        MapSqlParameterSource userParams = new MapSqlParameterSource()
                .addValue("userId", user.getUserId())
                .addValue("username", user.getUsername())
                .addValue("password", user.getPassword())
                .addValue("email", user.getEmail())
                .addValue("status", user.getStatus())
                .addValue("provider", user.getProvider())
                .addValue("providerId", user.getProviderId())
                .addValue("lastLogin", user.getLastLogin())
                .addValue("mfaEnabled", user.isMfaEnabled() ? 1 : 0) // Chuyển boolean thành 0 hoặc 1
                .addValue("tokenVersion", user.getTokenVersion());

        try {
            namedParameterJdbcTemplate.update(userSql, userParams);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error inserting user into database", e);
        }

        // Lưu roles
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            String rolesSql = "INSERT INTO user_roles (user_id, role) VALUES (:userId, :role)";
            for (String role : user.getRoles()) {
                MapSqlParameterSource roleParams = new MapSqlParameterSource()
                        .addValue("userId", user.getUserId())
                        .addValue("role", role);
                try {
                    namedParameterJdbcTemplate.update(rolesSql, roleParams);
                } catch (DataAccessException e) {
                    throw new RuntimeException("Error inserting user roles into database", e);
                }
            }
        }

        // Lưu permissions
        if (user.getPermissions() != null && !user.getPermissions().isEmpty()) {
            String permissionsSql = "INSERT INTO user_permissions (user_id, permission) VALUES (:userId, :permission)";
            for (String permission : user.getPermissions()) {
                MapSqlParameterSource permissionParams = new MapSqlParameterSource()
                        .addValue("userId", user.getUserId())
                        .addValue("permission", permission);
                try {
                    namedParameterJdbcTemplate.update(permissionsSql, permissionParams);
                } catch (DataAccessException e) {
                    throw new RuntimeException("Error inserting user permissions into database", e);
                }
            }
        }
    }


    /**
     * Cập nhật thông tin username , email , status , provider, provider_id trong database
     * @param user
     * */
    public void update(User user) {
        String sql = "UPDATE users SET username = :username, email = :email, status = :status, provider = :provider " +
                "WHERE provider_id = :providerId AND provider = :provider";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", user.getUsername())
                .addValue("email", user.getEmail())
                .addValue("status", user.getStatus())
                .addValue("provider", user.getProvider())
                .addValue("providerId", user.getProviderId());

        namedParameterJdbcTemplate.update(sql, params);
    }

    /**
     * Cập nhật thuộc tính tuỳ ý của người dùng, phải cung cấp userId
     * @param information
     * @return UserDto
     * */
    @Transactional
    public UserDto update(Map<String, ?> information) {
        if (!information.containsKey("userId")) {
            throw new IllegalArgumentException("userId is required");
        }

        String userId = information.get("userId").toString();
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);

        boolean joinUserRoles = information.containsKey("roles");
        boolean joinUserPermissions = information.containsKey("permissions");

        StringJoiner setClause = new StringJoiner(", ");
        information.forEach((key, value) -> {
            if (!"userId".equals(key) && !"roles".equals(key) && !"permissions".equals(key)) {
                String columnName = camelCaseToSnakeCase(key);
                setClause.add(columnName + " = :" + columnName);
                params.put(columnName, value);
            }
        });

        if(setClause.length() > 0) {
            String sql = "UPDATE users SET " + setClause + " WHERE user_id = :user_id";
            namedParameterJdbcTemplate.update(sql, params);
        }


        if (joinUserRoles) {
            List<String> roles = (List<String>) information.get("roles");
            updateUserRoles(userId, roles);
        }
        if (joinUserPermissions) {
            List<String> permissions = (List<String>) information.get("permissions");
            updateUserPermissions(userId, permissions);
        }

        return new UserDto(UserDto.Status.OK);
    }

    private void updateUserRoles(String userId, List<String> roles) {
//        String deleteSql = "DELETE FROM user_roles WHERE user_id = :user_id";
//        namedParameterJdbcTemplate.update(deleteSql, Map.of("user_id", userId));
        // Thêm vai trò mới
        String insertSql = "INSERT INTO user_roles (user_id, role) VALUES (:user_id, :role)";
        for (String role : roles) {
            Map<String, Object> roleParams = new HashMap<>();
            roleParams.put("user_id", userId);
            roleParams.put("role", role);
            namedParameterJdbcTemplate.update(insertSql, roleParams);
        }
    }

    private void updateUserPermissions(String userId, List<String> permissions) {
//        String deleteSql = "DELETE FROM user_permissions WHERE user_id = :user_id";
//        namedParameterJdbcTemplate.update(deleteSql, Map.of("user_id", userId));
        String insertSql = "INSERT INTO user_permissions (user_id, permission) VALUES (:user_id, :permission)";
        for (String permission : permissions) {
            Map<String, Object> permissionParams = new HashMap<>();
            permissionParams.put("user_id", userId);
            permissionParams.put("permission", permission);
            namedParameterJdbcTemplate.update(insertSql, permissionParams);
        }
    }

    private String camelCaseToSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    public void saveToken(UserToken userToken) {
        String sql = "INSERT INTO usertoken (id, username, email, token, tokenExpiry) VALUES (:id, :username, :email, :token, :tokenExpiry)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", userToken.getId())
                .addValue("username", userToken.getUsername())
                .addValue("email", userToken.getEmail())
                .addValue("token", userToken.getToken())
                .addValue("tokenExpiry", userToken.getTokenExpiry());
        namedParameterJdbcTemplate.update(sql, params);
    }


    /**
     * Tìm kiếm người dùng theo reset password token
     * @param token
     * @return Optional<UserToken>
     * */
    public Optional<UserToken> findByToken(String token){
        String sql = "SELECT * FROM usertoken WHERE token = :token";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("token", token);
        return namedParameterJdbcTemplate.query(sql, params, rs -> {
            if (rs.next()) {
                return Optional.of(new UserToken(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("token"),
                        rs.getLong("tokenExpiry")
                ));
            }
            return Optional.empty();
        });
    }

    /**
     * Xoá reset password token theo userId, sau khi khôi phục mật khâu
     * @param userId
     * */
    public void deleteTokenByUserId(String userId) {
        String sql = "DELETE FROM usertoken WHERE id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public int deleteExpiredTokens(long currentUnixTime) {
        String sql = "DELETE FROM usertoken WHERE tokenExpiry < :currentUnixTime";
        MapSqlParameterSource params = new MapSqlParameterSource("currentUnixTime", currentUnixTime);
        return namedParameterJdbcTemplate.update(sql, params);
    }

    /**
     * Tìm người dùng theo provider_id, provider (Google)
     * @param providerId
     * @param provider
     * @return Optional<User>
     * */
    public Optional<User> findByProviderIdAndProvider(String providerId, String provider) {
        String sql = """
        SELECT u.*, 
               GROUP_CONCAT(DISTINCT ur.role) AS roles, 
               GROUP_CONCAT(DISTINCT up.permission) AS permissions
        FROM users u
        LEFT JOIN user_roles ur ON u.user_id = ur.user_id
        LEFT JOIN user_permissions up ON u.user_id = up.user_id
        WHERE u.provider_id = :providerId AND u.provider = :provider
        GROUP BY u.user_id
    """;

        Map<String, Object> params = new HashMap<>();
        params.put("providerId", providerId);
        params.put("provider", provider);

        try {
            User user = namedParameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                User u = new User();
                u.setUserId(rs.getString("user_id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setEmail(rs.getString("email"));

                // Tách vai trò và quyền từ chuỗi thành danh sách
                String roles = rs.getString("roles");
                u.setRoles(roles != null ? Arrays.asList(roles.split(",")) : new ArrayList<>());

                String permissions = rs.getString("permissions");
                u.setPermissions(permissions != null ? Arrays.asList(permissions.split(",")) : new ArrayList<>());

                u.setStatus(rs.getString("status"));
                u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                u.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                u.setLastLogin(rs.getTimestamp("last_login") != null ? rs.getTimestamp("last_login").toLocalDateTime() : null);
                u.setMfaEnabled(rs.getBoolean("mfa_enabled"));
                u.setTokenVersion(rs.getInt("token_version"));
                u.setProvider(rs.getString("provider"));
                u.setProviderId(rs.getString("provider_id"));
                return u;
            });
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public User findById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = :userId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);

        List<User> users = namedParameterJdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> {
            String userId1 = rs.getString("user_id");
            List<String> roles = getRolesByUserId(userId1);
            List<String> permissions = getPermissionsByUserId(userId1);
            return new User(
                    userId1,
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

        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * Tìm
     * */
    public List<String> getRolesByUserId(String userId) {
        String sql = "SELECT role FROM user_roles WHERE user_id = :userId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);

        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, rowNum) ->
                rs.getString("role")
        );
    }

    /**
     * Lấy permissions theo user_id, dùng để kiểm tra về quyền của người dùng như Teacher , ....
     * @param userId
     * @return List<String>
     * */
    public List<String> getPermissionsByUserId(String userId) {
        String sql = "SELECT permission FROM user_permissions WHERE user_id = :userId";
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);

        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, rowNum) ->
                rs.getString("permission")
        );
    }


    public String getUserIdByUsername(String username) {
        String sql = "SELECT user_id FROM users WHERE username = :username";

        MapSqlParameterSource namedParameters = new MapSqlParameterSource("username", username);

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, String.class);
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("User not found for username: " + username);
        }
    }
}
