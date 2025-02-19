package com.nhson.authservice.controller;

import com.nhson.authservice.authentication.JwtAuthentication;
import com.nhson.authservice.service.AuthService;
import com.nhson.authservice.user.User;
import com.nhson.authservice.user.UserDto;
import com.nhson.authservice.user.UserRepository;
import com.nhson.authservice.user.UserToken;
import com.nhson.authservice.validator.ValidLoginRequest;
import com.nhson.authservice.validator.ValidRegisterRequest;
import com.nhson.authservice.validator.ValidUpdateRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @ValidRegisterRequest Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");
        String email = requestBody.get("email");

        String token = authService.register(username, password, email);
        return ResponseEntity.ok(token);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @ValidLoginRequest Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");
        String token = authService.login(username, password);
        return ResponseEntity.ok(token);
    }

    /**
     * Gửi magic link để đặt lại mật khẩu
     *
     * @param email email để gửi magic link
     * @return
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Email(message = "Email is not valid") @RequestParam("email") String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Could not find any emails associated with the account"));
        if (user != null) {
            String magicLink = authService.generateMagicLink(user);
            return ResponseEntity.ok("Magic link has been sent to your email , " + magicLink);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
    @GetMapping("/reset-password")
    public ResponseEntity<?> getUpdatePasswordPage(@RequestParam("token") @NotBlank(message = "Token is required for reset password") String token,
                                                   @RequestParam("userId") @NotBlank(message = "userId is required for reset password") String userId) {
        Optional<UserToken> userToken = userRepository.findByToken(token);

        if (userToken.isPresent() && userToken.get().getTokenExpiry() > System.currentTimeMillis()) {
            if (userToken.get().getId().equals(userId)) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/html")
                        .body(new ClassPathResource("static/update-password.html"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID mismatch.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody @ValidUpdateRequest Map<String, String> body) {
        String token = body.get("token");
        String userId = body.get("userId");
        String newPassword = body.get("newPassword");

        Optional<UserToken> userToken = userRepository.findByToken(token);
        if (userToken.isPresent() && userToken.get().getTokenExpiry() > System.currentTimeMillis()) {
            if (userToken.get().getId().equals(userId)) {
                User user = userRepository.findByEmail(userToken.get().getEmail()).orElseThrow(() -> new UsernameNotFoundException("Could not find any emails associated with the account"));
                authService.updatePassword(user, newPassword);
                return ResponseEntity.ok("Password updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID mismatch.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token.");
        }
    }

    /**
     * Cập nhật thông tin người dùng
     * @param information map chứa thông tin cần cập nhât, buộc phải có key userId
     * @return UserDto chứa id của người dùng được cập nhật
     */
    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUserInfo(@RequestBody @ValidUpdateRequest Map<String,?> information) {
        UserDto dto = userRepository.update(information);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @GetMapping("/permission")
    public ResponseEntity<List<String>> getPermission(JwtAuthentication authentication) throws Exception {
        List<String> permissions = userRepository.getPermissionsByUserId(authentication.getName());
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }

    @GetMapping("/userId")
    public ResponseEntity<String> getUserIdByUsername(@RequestParam("username")String username){
        String userId = userRepository.getUserIdByUsername(username);
        return ResponseEntity.ok(userId);
    }

}
