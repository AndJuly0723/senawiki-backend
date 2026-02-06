package com.senawiki.guide.api.dto;

public class GuideDeckSlotCreateRequest {

    private int position;
    private String heroId;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getHeroId() {
        return heroId;
    }

    public void setHeroId(String heroId) {
        this.heroId = heroId;
    }
}
