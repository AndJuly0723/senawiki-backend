package com.senawiki.community.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
