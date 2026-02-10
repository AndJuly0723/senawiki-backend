package com.senawiki.pet.service;

import com.senawiki.pet.api.dto.PetRequest;
import com.senawiki.pet.api.dto.PetResponse;
import com.senawiki.pet.api.dto.PetSkillRequest;
import com.senawiki.pet.api.dto.PetSkillResponse;
import com.senawiki.pet.domain.Pet;
import com.senawiki.pet.domain.PetGrade;
import com.senawiki.pet.domain.PetRepository;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PetService {

    private final PetRepository repository;

    public PetService(PetRepository repository) {
        this.repository = repository;
    }

    public PetResponse create(PetRequest request) {
        requireId(request.getId());
        if (repository.existsById(request.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pet id already exists");
        }
        validateSkill(request.getSkill());
        Pet pet = new Pet();
        apply(request, pet);
        return toResponse(repository.save(pet));
    }

    public PetResponse update(String id, PetRequest request) {
        Pet pet = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));
        if (request.getId() != null && !request.getId().isBlank() && !id.equals(request.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pet id cannot be changed");
        }
        validateSkill(request.getSkill());
        apply(request, pet);
        return toResponse(pet);
    }

    public void delete(String id) {
        Pet pet = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));
        repository.delete(pet);
    }

    @Transactional(readOnly = true)
    public List<PetResponse> list() {
        return repository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public PetResponse get(String id) {
        Pet pet = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));
        return toResponse(pet);
    }

    private void apply(PetRequest request, Pet pet) {
        if (pet.getId() == null) {
            pet.setId(request.getId());
        }
        pet.setName(request.getName());
        pet.setGrade(parseGrade(request.getGrade()));
        pet.setNickname(normalizeOptional(request.getNickname()));
        pet.setAcquisition(resolveAcquisition(request.getAcquisition()));
        pet.setImageKey(request.getImageKey());
        pet.setSkillImage(normalizeOptional(request.getSkillImage()));

        PetSkillRequest skill = request.getSkill();
        pet.setSkillName(normalizeOptional(skill.getName()));
        pet.setSkillTarget(normalizeOptional(skill.getTarget()));
        pet.setSkillDescriptionLines(normalizeLines(skill.getDescriptionLines()));
    }

    private void requireId(String id) {
        if (id == null || id.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pet id is required");
        }
    }

    private void validateSkill(PetSkillRequest skill) {
        if (skill == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pet skill is required");
        }
        if (skill.getName() == null || skill.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pet skill name is required");
        }
        if (skill.getTarget() == null || skill.getTarget().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pet skill target is required");
        }
        boolean hasLines = skill.getDescriptionLines() != null && !skill.getDescriptionLines().isEmpty();
        if (!hasLines) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Pet skill descriptionLines is required"
            );
        }
    }

    private PetGrade parseGrade(String value) {
        try {
            return PetGrade.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pet grade");
        }
    }

    private PetResponse toResponse(Pet pet) {
        PetResponse response = new PetResponse();
        response.setId(pet.getId());
        response.setName(pet.getName());
        response.setGrade(toLower(pet.getGrade()));
        response.setNickname(pet.getNickname());
        response.setAcquisition(pet.getAcquisition());
        response.setImageKey(pet.getImageKey());
        response.setSkillImage(pet.getSkillImage());
        response.setSkill(toSkillResponse(pet));
        return response;
    }

    private PetSkillResponse toSkillResponse(Pet pet) {
        PetSkillResponse response = new PetSkillResponse();
        response.setName(pet.getSkillName());
        response.setTarget(pet.getSkillTarget());
        response.setDescriptionLines(pet.getSkillDescriptionLines());
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

    private List<String> resolveAcquisition(List<String> values) {
        if (values != null && !values.isEmpty()) {
            return values;
        }
        return List.of("소환", "합성");
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    private List<String> normalizeLines(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values;
    }
}
