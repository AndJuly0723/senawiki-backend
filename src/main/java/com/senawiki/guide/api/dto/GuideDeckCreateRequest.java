package com.senawiki.guide.api.dto;

import com.senawiki.guide.domain.GuideType;
import com.senawiki.guide.domain.SiegeDay;
import java.util.List;

public class GuideDeckCreateRequest {

    private Long id;
    private Long deckId;
    private GuideType guideType;
    private String raidId;
    private String stageId;
    private String expeditionId;
    private SiegeDay siegeDay;
    private String detail;
    private Long counterParentDeckId;
    private Long parentDeckId;
    private Long counterOfDeckId;
    private Long sourceDeckId;
    private Long targetDeckId;
    private Boolean isCounter;
    private Boolean counter;
    private GuideDeckTeamCreateRequest team;
    private List<GuideDeckTeamCreateRequest> teams;
    private List<GuideDeckSkillOrderCreateRequest> skillOrders;
    private List<GuideDeckHeroEquipmentCreateRequest> heroEquipments;

    private boolean counterParentDeckIdProvided;
    private boolean parentDeckIdProvided;
    private boolean counterOfDeckIdProvided;
    private boolean sourceDeckIdProvided;
    private boolean targetDeckIdProvided;
    private boolean isCounterProvided;
    private boolean counterProvided;
    private boolean detailProvided;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }

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

    public String getExpeditionId() {
        return expeditionId;
    }

    public void setExpeditionId(String expeditionId) {
        this.expeditionId = expeditionId;
    }

    public SiegeDay getSiegeDay() {
        return siegeDay;
    }

    public void setSiegeDay(SiegeDay siegeDay) {
        this.siegeDay = siegeDay;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detailProvided = true;
        this.detail = detail;
    }

    public Long getCounterParentDeckId() {
        return counterParentDeckId;
    }

    public void setCounterParentDeckId(Long counterParentDeckId) {
        this.counterParentDeckIdProvided = true;
        this.counterParentDeckId = counterParentDeckId;
    }

    public Long getParentDeckId() {
        return parentDeckId;
    }

    public void setParentDeckId(Long parentDeckId) {
        this.parentDeckIdProvided = true;
        this.parentDeckId = parentDeckId;
    }

    public Long getCounterOfDeckId() {
        return counterOfDeckId;
    }

    public void setCounterOfDeckId(Long counterOfDeckId) {
        this.counterOfDeckIdProvided = true;
        this.counterOfDeckId = counterOfDeckId;
    }

    public Long getSourceDeckId() {
        return sourceDeckId;
    }

    public void setSourceDeckId(Long sourceDeckId) {
        this.sourceDeckIdProvided = true;
        this.sourceDeckId = sourceDeckId;
    }

    public Long getTargetDeckId() {
        return targetDeckId;
    }

    public void setTargetDeckId(Long targetDeckId) {
        this.targetDeckIdProvided = true;
        this.targetDeckId = targetDeckId;
    }

    public Boolean getIsCounter() {
        return isCounter;
    }

    public void setIsCounter(Boolean counter) {
        this.isCounterProvided = true;
        isCounter = counter;
    }

    public Boolean getCounter() {
        return counter;
    }

    public void setCounter(Boolean counter) {
        this.counterProvided = true;
        this.counter = counter;
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

    public boolean hasCounterInput() {
        return counterParentDeckIdProvided
            || parentDeckIdProvided
            || counterOfDeckIdProvided
            || sourceDeckIdProvided
            || targetDeckIdProvided
            || isCounterProvided
            || counterProvided;
    }

    public boolean isCounterParentDeckIdProvided() {
        return counterParentDeckIdProvided;
    }

    public boolean isParentDeckIdProvided() {
        return parentDeckIdProvided;
    }

    public boolean isCounterOfDeckIdProvided() {
        return counterOfDeckIdProvided;
    }

    public boolean isSourceDeckIdProvided() {
        return sourceDeckIdProvided;
    }

    public boolean isTargetDeckIdProvided() {
        return targetDeckIdProvided;
    }

    public boolean isIsCounterProvided() {
        return isCounterProvided;
    }

    public boolean isCounterProvided() {
        return counterProvided;
    }

    public boolean isDetailProvided() {
        return detailProvided;
    }
}
