package com.senawiki.guide.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuideDeckRepository extends JpaRepository<GuideDeck, Long> {

    Page<GuideDeck> findAllByGuideType(GuideType guideType, Pageable pageable);

    @Modifying
    @Query("update GuideDeck d set d.upVotes = d.upVotes + 1 where d.id = :deckId")
    int incrementUpVotes(@Param("deckId") Long deckId);

    @Modifying
    @Query("update GuideDeck d set d.downVotes = d.downVotes + 1 where d.id = :deckId")
    int incrementDownVotes(@Param("deckId") Long deckId);
}
