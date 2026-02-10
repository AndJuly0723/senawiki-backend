package com.senawiki.guide.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideDeckSlotRepository extends JpaRepository<GuideDeckSlot, Long> {

    List<GuideDeckSlot> findByTeamIdIn(List<Long> teamIds);

    void deleteByTeamIdIn(List<Long> teamIds);

    Optional<GuideDeckSlot> findByTeamIdAndSlotNoAndIsPet(Long teamId, int slotNo, boolean isPet);
}
