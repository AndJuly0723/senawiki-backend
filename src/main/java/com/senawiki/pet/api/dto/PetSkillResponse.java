package com.senawiki.pet.api.dto;

import java.util.List;

public class PetSkillResponse {

    private String name;
    private String target;
    private List<String> descriptionLines;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<String> getDescriptionLines() {
        return descriptionLines;
    }

    public void setDescriptionLines(List<String> descriptionLines) {
        this.descriptionLines = descriptionLines;
    }
}
