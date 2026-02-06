package com.senawiki.guide.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideDeckHeroEquipmentRepository extends JpaRepository<GuideDeckHeroEquipment, Long> {

    Optional<GuideDeckHeroEquipment> findByTeamIdAndHeroId(Long teamId, String heroId);

    Optional<GuideDeckHeroEquipment> findByTeamIdInAndHeroId(List<Long> teamIds, String heroId);

    void deleteByTeamIdIn(List<Long> teamIds);
}
