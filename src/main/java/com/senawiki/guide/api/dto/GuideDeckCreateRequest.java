package com.senawiki.guide.api.dto;

import com.senawiki.guide.domain.GuideType;
import java.util.List;

public class GuideDeckCreateRequest {

    private GuideType guideType;
    private String raidId;
    private String stageId;
    private GuideDeckTeamCreateRequest team;
    private List<GuideDeckTeamCreateRequest> teams;
    private List<GuideDeckSkillOrderCreateRequest> skillOrders;
    private List<GuideDeckHeroEquipmentCreateRequest> heroEquipments;

    public GuideType getGuideType() {
        return guideType;
    }

    public void setGuideType(GuideType guideType) {
        this.guideType = guideType;
    }

    public String getRaidId() {
        return raidId;
    }

    public void setRaidId(String raidId) {
        this.raidId = raidId;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public GuideDeckTeamCreateRequest getTeam() {
        return team;
    }

    public void setTeam(GuideDeckTeamCreateRequest team) {
        this.team = team;
    }

    public List<GuideDeckTeamCreateRequest> getTeams() {
        return teams;
    }

    public void setTeams(List<GuideDeckTeamCreateRequest> teams) {
        this.teams = teams;
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
