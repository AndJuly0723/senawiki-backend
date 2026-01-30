package com.senawiki.community.service;

import com.senawiki.community.api.dto.CommunityCreateRequest;
import com.senawiki.community.api.dto.CommunityResponse;
import com.senawiki.community.api.dto.CommunitySummaryResponse;
import com.senawiki.community.api.dto.CommunityUpdateRequest;
import com.senawiki.community.domain.AuthorType;
import com.senawiki.community.domain.BoardType;
import com.senawiki.community.domain.CommunityPost;
import com.senawiki.community.domain.CommunityPostRepository;
import com.senawiki.user.domain.User;
import com.senawiki.user.domain.UserRepository;
import java.time.Duration;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class CommunityPostService {

    private final CommunityPostRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;

    public CommunityPostService(
        CommunityPostRepository repository,
        PasswordEncoder passwordEncoder,
        FileStorageService fileStorageService,
        UserRepository userRepository
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
    }

    public CommunityResponse create(BoardType boardType, CommunityCreateRequest request, MultipartFile file) {
        if (request.isNotice() && !isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin can create notices");
        }

        CommunityPost post = new CommunityPost();
        post.setBoardType(boardType);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setNotice(request.isNotice());

        Optional<String> memberUsername = resolveMemberUsername();
        if (memberUsername.isPresent()) {
            post.setAuthorType(AuthorType.MEMBER);
            String email = memberUsername.get();
            post.setMemberUsername(email);
            post.setAuthorName(resolveMemberDisplayName(email));
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
    public CommunityResponse get(BoardType boardType, Long id) {
        return toResponse(getPost(boardType, id));
    }

    public CommunityResponse incrementView(BoardType boardType, Long id) {
        CommunityPost post = getPost(boardType, id);
        post.setViewCount(post.getViewCount() + 1);
        return toResponse(post);
    }

    @Transactional(readOnly = true)
    public Page<CommunitySummaryResponse> list(BoardType boardType, Pageable pageable) {
        Pageable sorted = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Sort.by(Sort.Order.desc("notice"), Sort.Order.desc("createdAt"))
        );
        return repository.findAllByBoardTypeIncludingLegacy(boardType, sorted).map(this::toSummary);
    }

    public CommunityResponse update(BoardType boardType, Long id, CommunityUpdateRequest request, MultipartFile file) {
        CommunityPost post = getPost(boardType, id);
        if (post.isNotice() && !isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin can modify notices");
        }
        validateAuthor(post, request.getGuestName(), request.getGuestPassword());

        post.setBoardType(boardType);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        attachFile(post, file);
        return toResponse(post);
    }

    public void delete(BoardType boardType, Long id, String guestName, String guestPassword) {
        CommunityPost post = getPost(boardType, id);
        if (post.isNotice() && !isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin can delete notices");
        }
        if (!isAdmin()) {
            validateAuthor(post, guestName, guestPassword);
        }
        removeFileIfExists(post);
        repository.delete(post);
    }

    @Transactional(readOnly = true)
    public Optional<FileDownload> loadFile(BoardType boardType, Long id) {
        CommunityPost post = getPost(boardType, id);
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

    private String resolveMemberDisplayName(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            String nickname = user.get().getNickname();
            if (nickname != null && !nickname.isBlank()) {
                return nickname;
            }
            String name = user.get().getName();
            if (name != null && !name.isBlank()) {
                return name;
            }
        }
        return email;
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
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

    private CommunityPost getPost(BoardType boardType, Long id) {
        CommunityPost post = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        BoardType stored = post.getBoardType();
        if (stored == null && boardType == BoardType.COMMUNITY) {
            return post;
        }
        if (stored != boardType) {
            throw new IllegalArgumentException("Post not found");
        }
        return post;
    }

    private CommunityResponse toResponse(CommunityPost post) {
        CommunityResponse response = new CommunityResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setAuthorType(post.getAuthorType().name());
        response.setAuthorName(post.getAuthorName());
        response.setViewCount(post.getViewCount());
        response.setNotice(post.isNotice());
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
        response.setNotice(post.isNotice());
        response.setCreatedAt(post.getCreatedAt());
        return response;
    }
}
