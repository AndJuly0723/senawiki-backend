package com.senawiki.community.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    @Query("""
        select p
        from CommunityPost p
        where p.boardType = :boardType
           or (p.boardType is null and :boardType = com.senawiki.community.domain.BoardType.COMMUNITY)
        """)
    Page<CommunityPost> findAllByBoardTypeIncludingLegacy(
        @Param("boardType") BoardType boardType,
        Pageable pageable
    );

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByFileStoragePathIsNotNull();

    long countByFileStoragePathIsNotNullAndCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
