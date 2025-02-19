package com.nhson.classservice.application;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class InviteCodeManager {

    private static final String ALGORITHM = "AES";
    private SecretKey secretKey;

    @Value("${security.aes.key}")
    private String base64Key;

    public InviteCodeManager() {
    }
    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        if (decodedKey.length != 16 && decodedKey.length != 24 && decodedKey.length != 32) {
            throw new IllegalArgumentException("Invalid AES key length. Must be 16, 24, or 32 bytes.");
        }
        this.secretKey = new SecretKeySpec(decodedKey, ALGORITHM);
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public String generateInviteToken(String classId) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(classId.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }
    public String decodeInviteToken(String inviteToken) throws Exception {
        // Initialize Cipher for decryption
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decoded = Base64.getDecoder().decode(inviteToken);
        return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
    }
}
