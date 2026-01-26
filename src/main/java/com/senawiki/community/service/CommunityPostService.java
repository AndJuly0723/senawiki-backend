package com.senawiki.community.service;

import com.senawiki.community.api.dto.CommunityCreateRequest;
import com.senawiki.community.api.dto.CommunityResponse;
import com.senawiki.community.api.dto.CommunitySummaryResponse;
import com.senawiki.community.api.dto.CommunityUpdateRequest;
import com.senawiki.community.domain.AuthorType;
import com.senawiki.community.domain.CommunityPost;
import com.senawiki.community.domain.CommunityPostRepository;
import java.time.Duration;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class CommunityPostService {

    private final CommunityPostRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public CommunityPostService(
        CommunityPostRepository repository,
        PasswordEncoder passwordEncoder,
        FileStorageService fileStorageService
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }

    public CommunityResponse create(CommunityCreateRequest request, MultipartFile file) {
        CommunityPost post = new CommunityPost();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        Optional<String> memberUsername = resolveMemberUsername();
        if (memberUsername.isPresent()) {
            post.setAuthorType(AuthorType.MEMBER);
            post.setAuthorName(memberUsername.get());
            post.setMemberUsername(memberUsername.get());
        } else {
            requireGuestCredentials(request.getGuestName(), request.getGuestPassword());
            post.setAuthorType(AuthorType.GUEST);
            post.setAuthorName(request.getGuestName());
            post.setGuestPasswordHash(passwordEncoder.encode(request.getGuestPassword()));
        }

        post.setViewCount(0);
        attachFile(post, file);
        return toResponse(repository.save(post));
    }

    @Transactional(readOnly = true)
    public CommunityResponse get(Long id) {
        return toResponse(getPost(id));
    }

    public CommunityResponse incrementView(Long id) {
        CommunityPost post = getPost(id);
        post.setViewCount(post.getViewCount() + 1);
        return toResponse(post);
    }

    @Transactional(readOnly = true)
    public Page<CommunitySummaryResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(this::toSummary);
    }

    public CommunityResponse update(Long id, CommunityUpdateRequest request, MultipartFile file) {
        CommunityPost post = getPost(id);
        validateAuthor(post, request.getGuestName(), request.getGuestPassword());

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        attachFile(post, file);
        return toResponse(post);
    }

    public void delete(Long id, String guestName, String guestPassword) {
        CommunityPost post = getPost(id);
        validateAuthor(post, guestName, guestPassword);
        removeFileIfExists(post);
        repository.delete(post);
    }

    @Transactional(readOnly = true)
    public Optional<FileDownload> loadFile(Long id) {
        CommunityPost post = getPost(id);
        if (post.getFileStoragePath() == null) {
            return Optional.empty();
        }
        String url = fileStorageService.presignGetUrl(post.getFileStoragePath(), Duration.ofMinutes(10));
        if (url == null) {
            return Optional.empty();
        }
        return Optional.of(new FileDownload(url, post.getFileOriginalName(), post.getFileContentType()));
    }

    private void validateAuthor(CommunityPost post, String guestName, String guestPassword) {
        if (post.getAuthorType() == AuthorType.MEMBER) {
            Optional<String> memberUsername = resolveMemberUsername();
            if (memberUsername.isEmpty() || !memberUsername.get().equals(post.getMemberUsername())) {
                throw new IllegalStateException("Only the author can modify this post");
            }
            return;
        }

        requireGuestCredentials(guestName, guestPassword);
        if (!post.getAuthorName().equals(guestName)
            || !passwordEncoder.matches(guestPassword, post.getGuestPasswordHash())) {
            throw new IllegalArgumentException("Guest credentials do not match");
        }
    }

    private void requireGuestCredentials(String name, String password) {
        if (name == null || name.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Guest name and password are required");
        }
    }

    private Optional<String> resolveMemberUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return Optional.ofNullable(authentication.getName());
    }

    private void attachFile(CommunityPost post, MultipartFile file) {
        FileStorageResult result = fileStorageService.store(file);
        if (result == null) {
            return;
        }
        removeFileIfExists(post);
        post.setFileOriginalName(result.getOriginalName());
        post.setFileStoragePath(result.getStoragePath());
        post.setFileContentType(result.getContentType());
        post.setFileSize(result.getSize());
    }

    private void removeFileIfExists(CommunityPost post) {
        if (post.getFileStoragePath() == null) {
            return;
        }
        fileStorageService.delete(post.getFileStoragePath());
    }

    private CommunityPost getPost(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    private CommunityResponse toResponse(CommunityPost post) {
        CommunityResponse response = new CommunityResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setAuthorType(post.getAuthorType().name());
        response.setAuthorName(post.getAuthorName());
        response.setViewCount(post.getViewCount());
        response.setFileOriginalName(post.getFileOriginalName());
        if (post.getFileStoragePath() != null) {
            response.setFileDownloadUrl("/api/community/" + post.getId() + "/file");
        }
        response.setFileContentType(post.getFileContentType());
        response.setFileSize(post.getFileSize());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        return response;
    }

    private CommunitySummaryResponse toSummary(CommunityPost post) {
        CommunitySummaryResponse response = new CommunitySummaryResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setAuthorType(post.getAuthorType().name());
        response.setAuthorName(post.getAuthorName());
        response.setViewCount(post.getViewCount());
        response.setCreatedAt(post.getCreatedAt());
        return response;
    }
}
