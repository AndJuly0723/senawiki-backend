package com.senawiki.guide.api;

import com.senawiki.guide.api.dto.GuideDeckCreateRequest;
import com.senawiki.guide.api.dto.GuideDeckEquipmentResponse;
import com.senawiki.guide.api.dto.GuideDeckSummaryResponse;
import com.senawiki.guide.api.dto.GuideDeckVoteRequest;
import com.senawiki.guide.api.dto.GuideDeckVoteResponse;
import com.senawiki.guide.domain.GuideType;
import com.senawiki.guide.domain.GuideDeckVoteType;
import com.senawiki.guide.service.GuideDeckService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GuideDeckController {

    private final GuideDeckService service;

    public GuideDeckController(GuideDeckService service) {
        this.service = service;
    }

    @PostMapping(value = "/deck_create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long create(@RequestBody GuideDeckCreateRequest request) {
        return service.create(request);
    }

    @DeleteMapping("/guide-decks/{deckId}")
    public void delete(@PathVariable Long deckId) {
        service.delete(deckId);
    }

    @GetMapping("/guide-decks")
    public Page<GuideDeckSummaryResponse> list(
        @RequestParam GuideType type,
        Pageable pageable
    ) {
        return service.list(type, pageable);
    }

    @GetMapping("/guide-decks/{deckId}/equipment")
    public GuideDeckEquipmentResponse equipment(
        @PathVariable Long deckId,
        @RequestParam String heroId
    ) {
        return service.getEquipment(deckId, heroId);
    }

    @PostMapping(value = "/guide-decks/{deckId}/votes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GuideDeckVoteResponse vote(
        @PathVariable Long deckId,
        @RequestBody GuideDeckVoteRequest request
    ) {
        GuideDeckVoteType voteType = request == null ? null : request.getVoteType();
        return service.vote(deckId, voteType);
    }
}
