package com.senawiki.guide.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideDeckSkillOrderRepository extends JpaRepository<GuideDeckSkillOrder, Long> {

    List<GuideDeckSkillOrder> findByTeamIdIn(List<Long> teamIds);
}
