package com.senawiki.guide.api.dto;

import com.senawiki.guide.domain.GuideDeckVoteType;

public class GuideDeckVoteRequest {

    private GuideDeckVoteType voteType;

    public GuideDeckVoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(GuideDeckVoteType voteType) {
        this.voteType = voteType;
    }
}
