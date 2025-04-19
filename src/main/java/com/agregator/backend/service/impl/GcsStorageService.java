package com.agregator.backend.service.impl;

import com.agregator.backend.service.StorageService;
import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GcsStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(GcsStorageService.class);

    private final Storage storage;
    private final String bucketName;

    @Autowired
    public GcsStorageService(Storage storage, @Value("${gcs.bucket.name}") String bucketName) {
        this.storage = storage;
        this.bucketName = bucketName;
        logger.info("GCS Storage Service initialized for bucket: {}", bucketName);
    }

    @Override
    public String uploadFile(MultipartFile file, String destinationBlobName) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Cannot upload empty file.");
        }

        logger.info("Uploading file '{}' to bucket '{}' as blob '{}'", 
                    file.getOriginalFilename(), bucketName, destinationBlobName);

        BlobId blobId = BlobId.of(bucketName, destinationBlobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(file.getContentType()) // Set content type
            // Set ACL to allow public read access (REMOVED: Conflicts with Uniform Bucket-Level Access)
            // Ensure bucket allows public access via IAM (e.g., allUsers with roles/storage.objectViewer)
            .build();

        try {
            // Upload the file
            storage.create(blobInfo, file.getBytes());
            logger.info("Successfully uploaded {} to {}", destinationBlobName, bucketName);
            
            // Return the public URL
            return getPublicUrl(destinationBlobName);
        } catch (StorageException e) {
            logger.error("Error uploading file to GCS: {}", e.getMessage(), e);
            throw new IOException("Failed to upload file to GCS.", e);
        }
    }

    @Override
    public boolean deleteFile(String blobName) {
         logger.info("Attempting to delete blob '{}' from bucket '{}'", blobName, bucketName);
         BlobId blobId = BlobId.of(bucketName, blobName);
         try {
            boolean deleted = storage.delete(blobId);
            if (deleted) {
                 logger.info("Successfully deleted blob '{}'", blobName);
            } else {
                 logger.warn("Blob '{}' not found or could not be deleted.", blobName);
            }
            return deleted;
         } catch (StorageException e) {
            logger.error("Error deleting blob '{}' from GCS: {}", blobName, e.getMessage(), e);
            return false;
         }
    }
    
    @Override
    public String getPublicUrl(String blobName) {
        // Standard public URL format for GCS
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, blobName);
    }
} 