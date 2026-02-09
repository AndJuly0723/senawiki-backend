package com.senawiki.admin.service;

import com.senawiki.admin.domain.AssetType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.exception.SdkClientException;

@Service
public class AssetsStorageService {

    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

    private static final Logger log = LoggerFactory.getLogger(AssetsStorageService.class);

    private final S3Client s3Client;
    private final String bucket;

    public AssetsStorageService(
        S3Client assetsS3Client,
        @Value("${app.assets.bucket:senawiki-assets}") String bucket
    ) {
        this.s3Client = assetsS3Client;
        this.bucket = bucket;
    }

    public String upload(MultipartFile file, AssetType type) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
        }
        if (type == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Asset type is required");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size exceeds 5MB");
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid content type");
        }

        String extension = resolveExtension(file);
        String key = type.getPrefix() + UUID.randomUUID() + "." + extension;

        PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key);
        if (contentType != null && !contentType.isBlank()) {
            requestBuilder.contentType(contentType);
        }

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(
                requestBuilder.build(),
                RequestBody.fromInputStream(inputStream, file.getSize())
            );
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read upload file");
        } catch (SdkClientException ex) {
            log.error("S3 client error while uploading asset (bucket={}, key={})", bucket, key, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "S3 client error");
        } catch (S3Exception ex) {
            log.error("S3 error while uploading asset (bucket={}, key={})", bucket, key, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload to S3");
        }

        return key;
    }

    private String resolveExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File name is required");
        }
        String extension = StringUtils.getFilenameExtension(filename);
        if (extension == null || extension.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File extension is required");
        }
        String normalized = extension.toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported file extension");
        }
        return normalized;
    }
}
