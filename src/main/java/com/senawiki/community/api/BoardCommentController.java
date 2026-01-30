package com.senawiki.community.api;

import com.senawiki.community.api.dto.CommentCreateRequest;
import com.senawiki.community.api.dto.CommentDeleteRequest;
import com.senawiki.community.api.dto.CommentResponse;
import com.senawiki.community.api.dto.CommentUpdateRequest;
import com.senawiki.community.domain.BoardType;
import com.senawiki.community.service.CommunityCommentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boards/{boardType}/{postId}/comments")
public class BoardCommentController {

    private final CommunityCommentService service;

    public BoardCommentController(CommunityCommentService service) {
        this.service = service;
    }

    @GetMapping
    public List<CommentResponse> list(
        @PathVariable BoardType boardType,
        @PathVariable Long postId
    ) {
        return service.list(boardType, postId);
    }

    @PostMapping
    public CommentResponse create(
        @PathVariable BoardType boardType,
        @PathVariable Long postId,
        @Validated @RequestBody CommentCreateRequest request
    ) {
        return service.create(boardType, postId, request);
    }

    @PutMapping("/{commentId}")
    public CommentResponse update(
        @PathVariable BoardType boardType,
        @PathVariable Long postId,
        @PathVariable Long commentId,
        @Validated @RequestBody CommentUpdateRequest request
    ) {
        return service.update(boardType, postId, commentId, request);
    }

    @DeleteMapping("/{commentId}")
    public void delete(
        @PathVariable BoardType boardType,
        @PathVariable Long postId,
        @PathVariable Long commentId,
        @RequestParam(required = false) String guestName,
        @RequestParam(required = false) String guestPassword,
        @Valid @RequestBody(required = false) CommentDeleteRequest request
    ) {
        String resolvedName = guestName != null ? guestName : (request != null ? request.getGuestName() : null);
        String resolvedPassword = guestPassword != null ? guestPassword : (request != null ? request.getGuestPassword() : null);
        service.delete(boardType, postId, commentId, resolvedName, resolvedPassword);
    }
}
