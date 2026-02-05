package com.senawiki.guide.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "guide_deck_hero_equipments")
public class GuideDeckHeroEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private GuideDeckTeam team;

    @Column(nullable = false, length = 50)
    private String heroId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode equipmentJson;

    public Long getId() {
        return id;
    }

    public GuideDeckTeam getTeam() {
        return team;
    }

    public void setTeam(GuideDeckTeam team) {
        this.team = team;
    }

    public String getHeroId() {
        return heroId;
    }

    public void setHeroId(String heroId) {
        this.heroId = heroId;
    }

    public JsonNode getEquipmentJson() {
        return equipmentJson;
    }

    public void setEquipmentJson(JsonNode equipmentJson) {
        this.equipmentJson = equipmentJson;
    }
}
