package com.nhson.examservice.firebase.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${practice-makes-perfect.firebase.config.path}")
    private String firebaseConfigPath;

    @Value("${practice-makes-perfect.firebase.storage.bucket}")
    private String firebaseStorageBucket;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream(firebaseConfigPath);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket(firebaseStorageBucket)
                .build();

        return FirebaseApp.initializeApp(options);
    }


}
