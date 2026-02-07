package com.senawiki.guide.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideDeckVoteRepository extends JpaRepository<GuideDeckVote, Long> {

    boolean existsByDeckIdAndUserId(Long deckId, Long userId);
}
