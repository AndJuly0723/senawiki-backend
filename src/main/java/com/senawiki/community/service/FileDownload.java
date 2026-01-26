package com.senawiki.community.service;

public class FileDownload {

    private final String url;
    private final String originalName;
    private final String contentType;

    public FileDownload(String url, String originalName, String contentType) {
        this.url = url;
        this.originalName = originalName;
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getContentType() {
        return contentType;
    }
}
