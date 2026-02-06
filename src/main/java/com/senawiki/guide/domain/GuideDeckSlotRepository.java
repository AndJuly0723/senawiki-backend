package com.senawiki.guide.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideDeckSlotRepository extends JpaRepository<GuideDeckSlot, Long> {

    List<GuideDeckSlot> findByTeamIdIn(List<Long> teamIds);
}
