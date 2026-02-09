package com.senawiki.pet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Convert(converter = PetGradeConverter.class)
    @Column(nullable = false, length = 20)
    private PetGrade grade;

    @Column(length = 200)
    private String nickname;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> acquisition;

    @Column(name = "image_key", length = 300)
    private String imageKey;

    @Column(name = "skill_image", length = 300)
    private String skillImage;

    @Column(name = "skill_name", length = 100)
    private String skillName;

    @Column(name = "skill_target", length = 100)
    private String skillTarget;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skill_description_lines", columnDefinition = "jsonb")
    private List<String> skillDescriptionLines;

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

    public PetGrade getGrade() {
        return grade;
    }

    public void setGrade(PetGrade grade) {
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

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillTarget() {
        return skillTarget;
    }

    public void setSkillTarget(String skillTarget) {
        this.skillTarget = skillTarget;
    }

    public List<String> getSkillDescriptionLines() {
        return skillDescriptionLines;
    }

    public void setSkillDescriptionLines(List<String> skillDescriptionLines) {
        this.skillDescriptionLines = skillDescriptionLines;
    }
}
