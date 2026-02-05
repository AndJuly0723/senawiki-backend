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
@Table(name = "guide_deck_skill_orders")
public class GuideDeckSkillOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private GuideDeckTeam team;

    @Column(nullable = false, length = 50)
    private String heroId;

    @Column(nullable = false)
    private int skillSlot;

    @Column(nullable = false)
    private int orderNo;

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

    public int getSkillSlot() {
        return skillSlot;
    }

    public void setSkillSlot(int skillSlot) {
        this.skillSlot = skillSlot;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }
}
