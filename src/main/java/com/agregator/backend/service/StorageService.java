package com.agregator.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    /**
     * Uploads a file to the configured GCS bucket and makes it publicly readable.
     *
     * @param file The file to upload.
     * @param destinationBlobName The desired name for the blob (file) in GCS (e.g., "tool-images/uuid.jpg").
     * @return The public URL of the uploaded file.
     * @throws IOException If an error occurs during upload.
     */
    String uploadFile(MultipartFile file, String destinationBlobName) throws IOException;

    /**
     * Deletes a file from the configured GCS bucket.
     *
     * @param blobName The name of the blob (file) to delete.
     * @return true if deletion was successful, false otherwise.
     */
    boolean deleteFile(String blobName);
    
    /**
     * Generates a public URL for a given blob name in the configured bucket.
     * 
     * @param blobName The name of the blob (file).
     * @return The public URL.
     */
    String getPublicUrl(String blobName);
} 