package com.senawiki.community.service;

import com.senawiki.community.api.dto.CommentCreateRequest;
import com.senawiki.community.api.dto.CommentResponse;
import com.senawiki.community.api.dto.CommentUpdateRequest;
import com.senawiki.community.domain.AuthorType;
import com.senawiki.community.domain.BoardType;
import com.senawiki.community.domain.CommunityComment;
import com.senawiki.community.domain.CommunityCommentRepository;
import com.senawiki.community.domain.CommunityPost;
import com.senawiki.community.domain.CommunityPostRepository;
import com.senawiki.user.domain.User;
import com.senawiki.user.domain.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommunityCommentService {

    private final CommunityPostRepository postRepository;
    private final CommunityCommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public CommunityCommentService(
        CommunityPostRepository postRepository,
        CommunityCommentRepository commentRepository,
        PasswordEncoder passwordEncoder,
        UserRepository userRepository
    ) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> list(BoardType boardType, Long postId) {
        CommunityPost post = getPost(boardType, postId);
        return commentRepository.findAllByPostIdOrderByCreatedAtAsc(post.getId()).stream()
            .map(this::toResponse)
            .toList();
    }

    public CommentResponse create(BoardType boardType, Long postId, CommentCreateRequest request) {
        CommunityPost post = getPost(boardType, postId);
        CommunityComment comment = new CommunityComment();
        comment.setPost(post);
        comment.setContent(request.getContent());

        Optional<String> memberUsername = resolveMemberUsername();
        if (memberUsername.isPresent()) {
            comment.setAuthorType(AuthorType.MEMBER);
            String email = memberUsername.get();
            comment.setMemberUsername(email);
            comment.setAuthorName(resolveMemberDisplayName(email));
        } else {
            requireGuestCredentials(request.getGuestName(), request.getGuestPassword());
            comment.setAuthorType(AuthorType.GUEST);
            comment.setAuthorName(request.getGuestName());
            comment.setGuestPasswordHash(passwordEncoder.encode(request.getGuestPassword()));
        }

        return toResponse(commentRepository.save(comment));
    }

    public CommentResponse update(
        BoardType boardType,
        Long postId,
        Long commentId,
        CommentUpdateRequest request
    ) {
        CommunityComment comment = getComment(boardType, postId, commentId);
        validateAuthor(comment, request.getGuestName(), request.getGuestPassword());
        comment.setContent(request.getContent());
        return toResponse(comment);
    }

    public void delete(BoardType boardType, Long postId, Long commentId, String guestName, String guestPassword) {
        CommunityComment comment = getComment(boardType, postId, commentId);
        validateAuthor(comment, guestName, guestPassword);
        commentRepository.delete(comment);
    }

    private void validateAuthor(CommunityComment comment, String guestName, String guestPassword) {
        if (comment.getAuthorType() == AuthorType.MEMBER) {
            Optional<String> memberUsername = resolveMemberUsername();
            if (memberUsername.isEmpty() || !memberUsername.get().equals(comment.getMemberUsername())) {
                throw new IllegalStateException("Only the author can modify this comment");
            }
            return;
        }

        requireGuestCredentials(guestName, guestPassword);
        if (!comment.getAuthorName().equals(guestName)
            || !passwordEncoder.matches(guestPassword, comment.getGuestPasswordHash())) {
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

    private CommunityPost getPost(BoardType boardType, Long postId) {
        CommunityPost post = postRepository.findById(postId)
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

    private CommunityComment getComment(BoardType boardType, Long postId, Long commentId) {
        CommunityComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        CommunityPost post = comment.getPost();
        if (post == null || !post.getId().equals(postId)) {
            throw new IllegalArgumentException("Comment not found");
        }
        BoardType stored = post.getBoardType();
        if (stored == null && boardType == BoardType.COMMUNITY) {
            return comment;
        }
        if (stored != boardType) {
            throw new IllegalArgumentException("Comment not found");
        }
        return comment;
    }

    private CommentResponse toResponse(CommunityComment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPost().getId());
        response.setContent(comment.getContent());
        response.setAuthorType(comment.getAuthorType().name());
        response.setAuthorName(comment.getAuthorName());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        return response;
    }
}
