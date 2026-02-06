package com.senawiki.guide.api.dto;

import java.util.List;

public class GuideDeckHeroEquipmentCreateRequest {

    private String heroId;
    private String equipmentSet;
    private String ring;
    private List<GuideDeckEquipmentSlotCreateRequest> slots;

    public String getHeroId() {
        return heroId;
    }

    public void setHeroId(String heroId) {
        this.heroId = heroId;
    }

    public String getEquipmentSet() {
        return equipmentSet;
    }

    public void setEquipmentSet(String equipmentSet) {
        this.equipmentSet = equipmentSet;
    }

    public String getRing() {
        return ring;
    }

    public void setRing(String ring) {
        this.ring = ring;
    }

    public List<GuideDeckEquipmentSlotCreateRequest> getSlots() {
        return slots;
    }

    public void setSlots(List<GuideDeckEquipmentSlotCreateRequest> slots) {
        this.slots = slots;
    }
}
