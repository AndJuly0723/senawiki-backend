package com.senawiki.hero.service;

import com.senawiki.hero.api.dto.HeroRequest;
import com.senawiki.hero.api.dto.HeroResponse;
import com.senawiki.hero.domain.Hero;
import com.senawiki.hero.domain.HeroGrade;
import com.senawiki.hero.domain.HeroRepository;
import com.senawiki.hero.domain.HeroType;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class HeroService {

    private final HeroRepository repository;

    public HeroService(HeroRepository repository) {
        this.repository = repository;
    }

    public HeroResponse create(HeroRequest request) {
        requireId(request.getId());
        if (repository.existsById(request.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hero id already exists");
        }
        validateSkill2(request);
        Hero hero = new Hero();
        apply(request, hero);
        return toResponse(repository.save(hero));
    }

    public HeroResponse update(String id, HeroRequest request) {
        Hero hero = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hero not found"));
        if (request.getId() != null && !request.getId().isBlank() && !id.equals(request.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hero id cannot be changed");
        }
        validateSkill2(request);
        apply(request, hero);
        return toResponse(hero);
    }

    public void delete(String id) {
        Hero hero = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hero not found"));
        repository.delete(hero);
    }

    @Transactional(readOnly = true)
    public List<HeroResponse> list() {
        return repository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public HeroResponse get(String id) {
        Hero hero = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hero not found"));
        return toResponse(hero);
    }

    private void apply(HeroRequest request, Hero hero) {
        if (hero.getId() == null) {
            hero.setId(request.getId());
        }
        hero.setName(request.getName());
        hero.setType(parseType(request.getType()));
        hero.setGrade(parseGrade(request.getGrade()));
        hero.setNickname(normalizeOptional(request.getNickname()));
        hero.setAcquisition(defaultList(request.getAcquisition()));
        hero.setUsage(defaultList(request.getUsage()));
        hero.setGear(defaultList(request.getGear()));
        hero.setImageKey(request.getImageKey());
        hero.setBasicSkillImage(normalizeOptional(request.getBasicSkillImage()));
        hero.setSkill1Image(normalizeOptional(request.getSkill1Image()));
        hero.setSkill2Image(normalizeOptional(request.getSkill2Image()));
        hero.setPassiveSkillImage(normalizeOptional(request.getPassiveSkillImage()));
        hero.setHasSkill2(request.isHasSkill2());
    }

    private void requireId(String id) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Hero id is required");
        }
    }

    private void validateSkill2(HeroRequest request) {
        if (request.isHasSkill2()) {
            if (request.getSkill2Image() == null || request.getSkill2Image().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "skill2Image is required when hasSkill2 is true");
            }
        }
    }

    private HeroType parseType(String value) {
        try {
            return HeroType.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hero type");
        }
    }

    private HeroGrade parseGrade(String value) {
        try {
            return HeroGrade.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hero grade");
        }
    }

    private HeroResponse toResponse(Hero hero) {
        HeroResponse response = new HeroResponse();
        response.setId(hero.getId());
        response.setName(hero.getName());
        response.setType(toLower(hero.getType()));
        response.setGrade(toLower(hero.getGrade()));
        response.setNickname(hero.getNickname());
        response.setAcquisition(hero.getAcquisition());
        response.setUsage(hero.getUsage());
        response.setGear(hero.getGear());
        response.setImageKey(hero.getImageKey());
        response.setBasicSkillImage(hero.getBasicSkillImage());
        response.setSkill1Image(hero.getSkill1Image());
        response.setSkill2Image(hero.getSkill2Image());
        response.setPassiveSkillImage(hero.getPassiveSkillImage());
        response.setHasSkill2(hero.isHasSkill2());
        return response;
    }

    private String toLower(Enum<?> value) {
        if (value == null) {
            return null;
        }
        return value.name().toLowerCase(Locale.ROOT);
    }

    private List<String> defaultList(List<String> values) {
        if (values == null) {
            return Collections.emptyList();
        }
        return values;
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }
}
