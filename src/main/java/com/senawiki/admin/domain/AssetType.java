package com.senawiki.admin.domain;

public enum AssetType {
    HERO("heroes/"),
    PET("pets/"),
    GUIDE("guides/"),
    UI("ui/");

    private final String prefix;

    AssetType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
