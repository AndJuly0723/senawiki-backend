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
@Table(name = "guide_deck_teams")
public class GuideDeckTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "deck_id", nullable = false)
    private GuideDeck deck;

    @Column(nullable = false)
    private int teamNo;

    @Column(nullable = false)
    private int teamSize;

    @Column(nullable = false, length = 30)
    private String formationType;

    public Long getId() {
        return id;
    }

    public GuideDeck getDeck() {
        return deck;
    }

    public void setDeck(GuideDeck deck) {
        this.deck = deck;
    }

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

    public String getFormationType() {
        return formationType;
    }

    public void setFormationType(String formationType) {
        this.formationType = formationType;
    }
}
