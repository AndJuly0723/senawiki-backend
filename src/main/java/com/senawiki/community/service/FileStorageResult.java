package com.senawiki.community.service;

public class FileStorageResult {

    private final String originalName;
    private final String storagePath;
    private final String contentType;
    private final long size;

    public FileStorageResult(String originalName, String storagePath, String contentType, long size) {
        this.originalName = originalName;
        this.storagePath = storagePath;
        this.contentType = contentType;
        this.size = size;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSize() {
        return size;
    }
}
