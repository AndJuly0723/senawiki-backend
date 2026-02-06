package com.senawiki.guide.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideDeckRepository extends JpaRepository<GuideDeck, Long> {

    Page<GuideDeck> findAllByGuideType(GuideType guideType, Pageable pageable);
}
