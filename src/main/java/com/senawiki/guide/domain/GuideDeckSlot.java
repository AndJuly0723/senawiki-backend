package com.senawiki.guide.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "guide_deck_slots")
public class GuideDeckSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private GuideDeckTeam team;

    @Column(nullable = false)
    private int slotNo;

    @Column(length = 50)
    private String heroId;

    @Column(nullable = false)
    private boolean isPet;

    @Column(length = 50)
    private String petId;

    public Long getId() {
        return id;
    }

    public GuideDeckTeam getTeam() {
        return team;
    }

    public void setTeam(GuideDeckTeam team) {
        this.team = team;
    }

    public int getSlotNo() {
        return slotNo;
    }

    public void setSlotNo(int slotNo) {
        this.slotNo = slotNo;
    }

    public String getHeroId() {
        return heroId;
    }

    public void setHeroId(String heroId) {
        this.heroId = heroId;
    }

    public boolean isPet() {
        return isPet;
    }

    public void setPet(boolean pet) {
        isPet = pet;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }
}
