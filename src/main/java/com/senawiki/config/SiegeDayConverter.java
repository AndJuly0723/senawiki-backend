package com.senawiki.config;

import com.senawiki.guide.domain.SiegeDay;
import java.util.Locale;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SiegeDayConverter implements Converter<String, SiegeDay> {

    @Override
    public SiegeDay convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return SiegeDay.valueOf(source.trim().toUpperCase(Locale.ROOT));
    }
}
