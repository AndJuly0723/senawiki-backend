package com.senawiki.hero.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "heroes")
public class Hero {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 200)
    private String nickname;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> usage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> gear;

    @Column(nullable = false, length = 300)
    private String image;

    @Convert(converter = HeroTypeConverter.class)
    @Column(nullable = false, length = 20)
    private HeroType type;

    @Column(nullable = false, length = 50)
    private String typeLabel;

    @Column(nullable = false, length = 300)
    private String typeIcon;

    @Convert(converter = HeroGradeConverter.class)
    @Column(nullable = false, length = 20)
    private HeroGrade grade;

    @Column(nullable = false, length = 50)
    private String gradeLabel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> acquisition;

    @Column(nullable = false)
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public HeroType getType() {
        return type;
    }

    public void setType(HeroType type) {
        this.type = type;
    }

    public String getTypeLabel() {
        return typeLabel;
    }

    public void setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
    }

    public String getTypeIcon() {
        return typeIcon;
    }

    public void setTypeIcon(String typeIcon) {
        this.typeIcon = typeIcon;
    }

    public HeroGrade getGrade() {
        return grade;
    }

    public void setGrade(HeroGrade grade) {
        this.grade = grade;
    }

    public String getGradeLabel() {
        return gradeLabel;
    }

    public void setGradeLabel(String gradeLabel) {
        this.gradeLabel = gradeLabel;
    }

    public List<String> getAcquisition() {
        return acquisition;
    }

    public void setAcquisition(List<String> acquisition) {
        this.acquisition = acquisition;
    }

    public boolean isHasSkill2() {
        return hasSkill2;
    }

    public void setHasSkill2(boolean hasSkill2) {
        this.hasSkill2 = hasSkill2;
    }
}
