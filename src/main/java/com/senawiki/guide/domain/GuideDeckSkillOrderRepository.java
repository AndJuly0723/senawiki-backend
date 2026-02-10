package com.senawiki.guide.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideDeckSkillOrderRepository extends JpaRepository<GuideDeckSkillOrder, Long> {

    List<GuideDeckSkillOrder> findByTeamIdIn(List<Long> teamIds);

    void deleteByTeamIdIn(List<Long> teamIds);

    Optional<GuideDeckSkillOrder> findByTeamIdAndOrderNo(Long teamId, Integer orderNo);
}
