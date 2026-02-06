package com.senawiki.guide.api.dto;

import java.util.List;

public class GuideDeckEquipmentResponse {

    private Long deckId;
    private String heroId;
    private String heroName;
    private String equipmentSet;
    private String ring;
    private List<EquipmentSlot> slots;

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
        this.deckId = deckId;
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

    public String getEquipmentSet() {
        return equipmentSet;
    }

    public void setEquipmentSet(String equipmentSet) {
        this.equipmentSet = equipmentSet;
    }

    public String getRing() {
        return ring;
    }

    public void setRing(String ring) {
        this.ring = ring;
    }

    public List<EquipmentSlot> getSlots() {
        return slots;
    }

    public void setSlots(List<EquipmentSlot> slots) {
        this.slots = slots;
    }

    public static class EquipmentSlot {
        private String slotId;
        private String main;
        private List<String> subs;

        public String getSlotId() {
            return slotId;
        }

        public void setSlotId(String slotId) {
            this.slotId = slotId;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public List<String> getSubs() {
            return subs;
        }

        public void setSubs(List<String> subs) {
            this.subs = subs;
        }
    }
}
