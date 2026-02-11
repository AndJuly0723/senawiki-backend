package com.senawiki.guide.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuideDeckVoteRepository extends JpaRepository<GuideDeckVote, Long> {

    boolean existsByDeckIdAndUserId(Long deckId, Long userId);

    List<GuideDeckVote> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
