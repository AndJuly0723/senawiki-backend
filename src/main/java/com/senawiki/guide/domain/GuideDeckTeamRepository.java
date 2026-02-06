package com.senawiki.guide.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideDeckTeamRepository extends JpaRepository<GuideDeckTeam, Long> {

    List<GuideDeckTeam> findByDeckId(Long deckId);

    List<GuideDeckTeam> findByDeckIdIn(List<Long> deckIds);
}
