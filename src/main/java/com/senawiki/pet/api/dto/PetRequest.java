package com.senawiki.pet.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class PetRequest {

    private String id;

    @NotBlank(message = "Pet name is required")
    private String name;

    @NotBlank(message = "Pet grade is required")
    private String grade;

    private String nickname;
    private List<String> acquisition;

    @NotBlank(message = "Pet imageKey is required")
    private String imageKey;

    private String skillImage;
    private PetSkillRequest skill;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<String> getAcquisition() {
        return acquisition;
    }

    public void setAcquisition(List<String> acquisition) {
        this.acquisition = acquisition;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public String getSkillImage() {
        return skillImage;
    }

    public void setSkillImage(String skillImage) {
        this.skillImage = skillImage;
    }

    public PetSkillRequest getSkill() {
        return skill;
    }

    public void setSkill(PetSkillRequest skill) {
        this.skill = skill;
    }
}
