package com.senawiki.hero.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Locale;

@Converter(autoApply = false)
public class HeroTypeConverter implements AttributeConverter<HeroType, String> {

    @Override
    public String convertToDatabaseColumn(HeroType attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute == HeroType.UNKNOWN) {
            return "unknown";
        }
        return attribute.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public HeroType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        if ("unknown".equalsIgnoreCase(dbData)) {
            return HeroType.UNKNOWN;
        }
        return HeroType.valueOf(dbData.toUpperCase(Locale.ROOT));
    }
}
