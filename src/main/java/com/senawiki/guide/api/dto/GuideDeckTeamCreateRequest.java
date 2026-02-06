package com.senawiki.guide.api.dto;

import java.util.List;

public class GuideDeckTeamCreateRequest {

    private Integer teamNo;
    private Integer teamSize;
    private String formationId;
    private String petId;
    private List<GuideDeckSlotCreateRequest> slots;
    private List<GuideDeckSkillOrderCreateRequest> skillOrders;
    private List<GuideDeckHeroEquipmentCreateRequest> heroEquipments;

    public Integer getTeamNo() {
        return teamNo;
    }

    public void setTeamNo(Integer teamNo) {
        this.teamNo = teamNo;
    }

    public Integer getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(Integer teamSize) {
        this.teamSize = teamSize;
    }

    public String getFormationId() {
        return formationId;
    }

    public void setFormationId(String formationId) {
        this.formationId = formationId;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public List<GuideDeckSlotCreateRequest> getSlots() {
        return slots;
    }

    public void setSlots(List<GuideDeckSlotCreateRequest> slots) {
        this.slots = slots;
    }

    public List<GuideDeckSkillOrderCreateRequest> getSkillOrders() {
        return skillOrders;
    }

    public void setSkillOrders(List<GuideDeckSkillOrderCreateRequest> skillOrders) {
        this.skillOrders = skillOrders;
    }

    public List<GuideDeckHeroEquipmentCreateRequest> getHeroEquipments() {
        return heroEquipments;
    }

    public void setHeroEquipments(List<GuideDeckHeroEquipmentCreateRequest> heroEquipments) {
        this.heroEquipments = heroEquipments;
    }
}
