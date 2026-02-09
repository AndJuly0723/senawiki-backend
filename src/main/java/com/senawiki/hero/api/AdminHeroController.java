package com.senawiki.hero.api;

import com.senawiki.hero.api.dto.HeroRequest;
import com.senawiki.hero.api.dto.HeroResponse;
import com.senawiki.hero.service.HeroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/heroes")
public class AdminHeroController {

    private final HeroService heroService;

    public AdminHeroController(HeroService heroService) {
        this.heroService = heroService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HeroResponse create(@Valid @RequestBody HeroRequest request) {
        return heroService.create(request);
    }

    @PutMapping("/{id}")
    public HeroResponse update(@PathVariable String id, @Valid @RequestBody HeroRequest request) {
        return heroService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        heroService.delete(id);
    }
}
