package com.senawiki.guide.api.dto;

import java.util.List;

public class GuideDeckEquipmentSlotCreateRequest {

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
