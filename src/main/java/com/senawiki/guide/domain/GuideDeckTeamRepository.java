package com.senawiki.guide.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideDeckTeamRepository extends JpaRepository<GuideDeckTeam, Long> {

    List<GuideDeckTeam> findByDeckId(Long deckId);

    List<GuideDeckTeam> findByDeckIdIn(List<Long> deckIds);

    Optional<GuideDeckTeam> findByDeckIdAndTeamNo(Long deckId, Integer teamNo);
}
