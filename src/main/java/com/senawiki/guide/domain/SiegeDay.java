package com.senawiki.guide.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

public enum SiegeDay {
    MON,
    TUE,
    WED,
    THU,
    FRI,
    SAT,
    SUN;

    @JsonCreator
    public static SiegeDay fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return SiegeDay.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }

    @JsonValue
    public String toJson() {
        return name().toLowerCase(Locale.ROOT);
    }
}
