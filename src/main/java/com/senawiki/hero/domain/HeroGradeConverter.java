package com.senawiki.hero.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Locale;

@Converter(autoApply = false)
public class HeroGradeConverter implements AttributeConverter<HeroGrade, String> {

    @Override
    public String convertToDatabaseColumn(HeroGrade attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute == HeroGrade.UNKNOWN) {
            return "unknown";
        }
        return attribute.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public HeroGrade convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        if ("unknown".equalsIgnoreCase(dbData)) {
            return HeroGrade.UNKNOWN;
        }
        return HeroGrade.valueOf(dbData.toUpperCase(Locale.ROOT));
    }
}
