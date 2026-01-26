package com.senawiki.community.api;

import com.senawiki.community.api.dto.CommunityCreateRequest;
import com.senawiki.community.api.dto.CommunityResponse;
import com.senawiki.community.api.dto.CommunitySummaryResponse;
import com.senawiki.community.api.dto.CommunityUpdateRequest;
import com.senawiki.community.service.CommunityPostService;
import com.senawiki.community.service.FileDownload;
import java.util.Optional;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/community")
public class CommunityPostController {

    private final CommunityPostService service;
    public CommunityPostController(CommunityPostService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommunityResponse createJson(@Validated @RequestBody CommunityCreateRequest request) {
        return service.create(request, null);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommunityResponse createMultipart(
        @Validated @RequestPart("request") CommunityCreateRequest request,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return service.create(request, file);
    }

    @GetMapping("/{id}")
    public CommunityResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping("/{id}/view")
    public CommunityResponse incrementView(@PathVariable Long id) {
        return service.incrementView(id);
    }

    @GetMapping
    public Page<CommunitySummaryResponse> list(Pageable pageable) {
        return service.list(pageable);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CommunityResponse updateJson(
        @PathVariable Long id,
        @Validated @RequestBody CommunityUpdateRequest request
    ) {
        return service.update(id, request, null);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommunityResponse updateMultipart(
        @PathVariable Long id,
        @Validated @RequestPart("request") CommunityUpdateRequest request,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return service.update(id, request, file);
    }

    @DeleteMapping("/{id}")
    public void delete(
        @PathVariable Long id,
        @RequestParam(required = false) String guestName,
        @RequestParam(required = false) String guestPassword
    ) {
        service.delete(id, guestName, guestPassword);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Void> downloadFile(@PathVariable Long id) {
        Optional<FileDownload> download = service.loadFile(id);
        if (download.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        FileDownload file = download.get();
        return ResponseEntity.status(302)
            .header(HttpHeaders.LOCATION, file.getUrl())
            .build();
    }
}
