package com.senawiki.pet.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Locale;

@Converter(autoApply = false)
public class PetGradeConverter implements AttributeConverter<PetGrade, String> {

    @Override
    public String convertToDatabaseColumn(PetGrade attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name().toLowerCase(Locale.ROOT);
    }

    @Override
    public PetGrade convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return PetGrade.valueOf(dbData.toUpperCase(Locale.ROOT));
    }
}
