package com.senawiki.community.service;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Service
public class FileStorageService {

    private final S3StorageProperties properties;
    private final S3Client s3Client;
    private final S3Presigner presigner;

    public FileStorageService(S3StorageProperties properties) {
        this.properties = properties;
        Region region = Region.of(properties.getRegion());
        this.s3Client = S3Client.builder().region(region).build();
        this.presigner = S3Presigner.builder().region(region).build();
    }

    public FileStorageResult store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        validateConfig();

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > -1) {
            extension = originalName.substring(dotIndex);
        }

        String key = buildKey(UUID.randomUUID() + extension);
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(properties.getBucket())
            .key(key)
            .contentType(file.getContentType())
            .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return new FileStorageResult(
                originalName,
                key,
                file.getContentType(),
                file.getSize()
            );
        } catch (IOException | S3Exception ex) {
            throw new IllegalStateException("Failed to store file in S3", ex);
        }
    }

    public void delete(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        validateConfig();
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .build());
        } catch (S3Exception ignored) {
            // Ignore delete failures to keep post flow resilient.
        }
    }

    public String presignGetUrl(String key, Duration ttl) {
        if (key == null || key.isBlank()) {
            return null;
        }
        validateConfig();
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(properties.getBucket())
            .key(key)
            .build();
        PresignedGetObjectRequest presigned = presigner.presignGetObject(
            GetObjectPresignRequest.builder()
                .signatureDuration(ttl)
                .getObjectRequest(getObjectRequest)
                .build()
        );
        return presigned.url().toString();
    }

    private String buildKey(String filename) {
        String prefix = properties.getPrefix();
        if (prefix == null || prefix.isBlank()) {
            return filename;
        }
        String normalized = prefix.endsWith("/") ? prefix : prefix + "/";
        return normalized + filename;
    }

    private void validateConfig() {
        if (properties.getBucket() == null || properties.getBucket().isBlank()) {
            throw new IllegalStateException("S3 bucket is not configured");
        }
        if (properties.getRegion() == null || properties.getRegion().isBlank()) {
            throw new IllegalStateException("S3 region is not configured");
        }
    }
}
