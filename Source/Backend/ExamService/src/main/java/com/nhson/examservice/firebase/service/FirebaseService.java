package com.nhson.examservice.firebase.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseService {
    public String uploadImageToFirebase(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }
        Bucket bucket = StorageClient.getInstance().bucket();;
        try {
            // Tạo blob (đối tượng trong Google Cloud Storage)
            Blob blob = bucket.create(UUID.randomUUID().toString(),file.getInputStream(),"images/*");

            return blob.getMediaLink();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred while uploading the file: " + e.getMessage(), e);
        }
    }
}
