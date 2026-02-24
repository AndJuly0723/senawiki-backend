package com.senawiki.guide.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GuideDeckRepository extends JpaRepository<GuideDeck, Long> {

    Page<GuideDeck> findAllByGuideType(GuideType guideType, Pageable pageable);

    Page<GuideDeck> findAllByGuideTypeAndRaidId(GuideType guideType, String raidId, Pageable pageable);

    Page<GuideDeck> findAllByGuideTypeAndStageId(GuideType guideType, String stageId, Pageable pageable);

    Page<GuideDeck> findAllByGuideTypeAndSiegeDay(GuideType guideType, SiegeDay siegeDay, Pageable pageable);

    Page<GuideDeck> findAllByGuideTypeAndExpeditionId(GuideType guideType, String expeditionId, Pageable pageable);

    Page<GuideDeck> findAllByGuideTypeAndCounterParentDeckId(
        GuideType guideType,
        Long counterParentDeckId,
        Pageable pageable
    );

    List<GuideDeck> findByAuthorUserId(Long userId);

    @Modifying
    @Query("update GuideDeck d set d.upVotes = d.upVotes + 1 where d.id = :deckId")
    int incrementUpVotes(@Param("deckId") Long deckId);

    @Modifying
    @Query("update GuideDeck d set d.downVotes = d.downVotes + 1 where d.id = :deckId")
    int incrementDownVotes(@Param("deckId") Long deckId);

    @Modifying
    @Query("update GuideDeck d set d.upVotes = d.upVotes - 1 where d.id = :deckId and d.upVotes > 0")
    int decrementUpVotes(@Param("deckId") Long deckId);

    @Modifying
    @Query("update GuideDeck d set d.downVotes = d.downVotes - 1 where d.id = :deckId and d.downVotes > 0")
    int decrementDownVotes(@Param("deckId") Long deckId);
}
