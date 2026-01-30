package com.senawiki.community.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    List<CommunityComment> findAllByPostIdOrderByCreatedAtAsc(Long postId);

    @Query("""
        select c.post.id as postId, count(c) as count
        from CommunityComment c
        where c.post.id in :postIds
        group by c.post.id
        """)
    List<PostCommentCount> countByPostIds(@Param("postIds") List<Long> postIds);

    interface PostCommentCount {
        Long getPostId();
        long getCount();
    }
}
