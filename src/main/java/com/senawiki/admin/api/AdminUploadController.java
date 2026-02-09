package com.senawiki.admin.api;

import com.senawiki.admin.api.dto.AssetUploadResponse;
import com.senawiki.admin.domain.AssetType;
import com.senawiki.admin.service.AssetsStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/assets")
public class AdminUploadController {

    private final AssetsStorageService assetsStorageService;

    public AdminUploadController(AssetsStorageService assetsStorageService) {
        this.assetsStorageService = assetsStorageService;
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public AssetUploadResponse upload(
        @RequestParam("file") MultipartFile file,
        @RequestParam("type") AssetType type
    ) {
        String imageKey = assetsStorageService.upload(file, type);
        AssetUploadResponse response = new AssetUploadResponse();
        response.setImageKey(imageKey);
        return response;
    }
}
