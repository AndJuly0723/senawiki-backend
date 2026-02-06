package com.senawiki.guide.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public class GuideDeckSummaryResponse {

    private Long id;
    private String guideType;
    private String authorNickname;
    private String authorRole;
    private int upVotes;
    private int downVotes;
    private LocalDateTime createdAt;
    private List<TeamSummary> teams;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuideType() {
        return guideType;
    }

    public void setGuideType(String guideType) {
        this.guideType = guideType;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    @JsonProperty("author")
    public String getAuthor() {
        return authorNickname;
    }

    @JsonProperty("writer")
    public String getWriter() {
        return authorNickname;
    }

    @JsonProperty("writerName")
    public String getWriterName() {
        return authorNickname;
    }

    @JsonProperty("nickname")
    public String getNickname() {
        return authorNickname;
    }

    @JsonProperty("userName")
    public String getUserName() {
        return authorNickname;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return authorNickname;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<TeamSummary> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamSummary> teams) {
        this.teams = teams;
    }

    public static class TeamSummary {
        private int teamNo;
        private int teamSize;
        private String formationId;
        private String petId;
        private List<SlotSummary> slots;
        private List<SkillOrderSummary> skillOrders;

        public int getTeamNo() {
            return teamNo;
        }

        public void setTeamNo(int teamNo) {
            this.teamNo = teamNo;
        }

        public int getTeamSize() {
            return teamSize;
        }

        public void setTeamSize(int teamSize) {
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

        public List<SlotSummary> getSlots() {
            return slots;
        }

        public void setSlots(List<SlotSummary> slots) {
            this.slots = slots;
        }

        public List<SkillOrderSummary> getSkillOrders() {
            return skillOrders;
        }

        public void setSkillOrders(List<SkillOrderSummary> skillOrders) {
            this.skillOrders = skillOrders;
        }

        @JsonProperty("skillOrder")
        public List<SkillOrderSummary> getSkillOrder() {
            return skillOrders;
        }

        @JsonProperty("skillSequence")
        public List<SkillOrderSummary> getSkillSequence() {
            return skillOrders;
        }

        @JsonProperty("skillList")
        public List<SkillOrderSummary> getSkillList() {
            return skillOrders;
        }
    }

    public static class SlotSummary {
        private int position;
        private String heroId;
        private String heroName;
        private String heroImage;
        private boolean isPet;
        private String petName;

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

        public String getHeroName() {
            return heroName;
        }

        public void setHeroName(String heroName) {
            this.heroName = heroName;
        }

        public String getHeroImage() {
            return heroImage;
        }

        public void setHeroImage(String heroImage) {
            this.heroImage = heroImage;
        }

        public boolean isPet() {
            return isPet;
        }

        public void setPet(boolean pet) {
            isPet = pet;
        }

        public String getPetName() {
            return petName;
        }

        public void setPetName(String petName) {
            this.petName = petName;
        }
    }

    public static class SkillOrderSummary {
        private String heroId;
        private String heroName;
        private int skill;
        private int order;

        public String getHeroId() {
            return heroId;
        }

        public void setHeroId(String heroId) {
            this.heroId = heroId;
        }

        public String getHeroName() {
            return heroName;
        }

        public void setHeroName(String heroName) {
            this.heroName = heroName;
        }

        public int getSkill() {
            return skill;
        }

        public void setSkill(int skill) {
            this.skill = skill;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }
    }
}
