package com.senawiki.hero.api;

import com.senawiki.hero.api.dto.HeroResponse;
import com.senawiki.hero.service.HeroService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/heroes")
public class HeroController {

    private final HeroService heroService;

    public HeroController(HeroService heroService) {
        this.heroService = heroService;
    }

    @GetMapping
    public List<HeroResponse> list() {
        return heroService.list();
    }

    @GetMapping("/{id}")
    public HeroResponse get(@PathVariable String id) {
        return heroService.get(id);
    }
}
