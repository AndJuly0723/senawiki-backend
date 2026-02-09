package com.senawiki.hero.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class HeroRequest {

    private String id;

    @NotBlank(message = "Hero name is required")
    private String name;

    @NotBlank(message = "Hero type is required")
    private String type;

    @NotBlank(message = "Hero grade is required")
    private String grade;

    private String nickname;
    private List<String> acquisition;
    private List<String> usage;
    private List<String> gear;

    @NotBlank(message = "Hero imageKey is required")
    private String imageKey;

    private String basicSkillImage;
    private String skill1Image;
    private String skill2Image;
    private String passiveSkillImage;
    private boolean hasSkill2;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public List<String> getUsage() {
        return usage;
    }

    public void setUsage(List<String> usage) {
        this.usage = usage;
    }

    public List<String> getGear() {
        return gear;
    }

    public void setGear(List<String> gear) {
        this.gear = gear;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public String getBasicSkillImage() {
        return basicSkillImage;
    }

    public void setBasicSkillImage(String basicSkillImage) {
        this.basicSkillImage = basicSkillImage;
    }

    public String getSkill1Image() {
        return skill1Image;
    }

    public void setSkill1Image(String skill1Image) {
        this.skill1Image = skill1Image;
    }

    public String getSkill2Image() {
        return skill2Image;
    }

    public void setSkill2Image(String skill2Image) {
        this.skill2Image = skill2Image;
    }

    public String getPassiveSkillImage() {
        return passiveSkillImage;
    }

    public void setPassiveSkillImage(String passiveSkillImage) {
        this.passiveSkillImage = passiveSkillImage;
    }

    public boolean isHasSkill2() {
        return hasSkill2;
    }

    public void setHasSkill2(boolean hasSkill2) {
        this.hasSkill2 = hasSkill2;
    }
}
