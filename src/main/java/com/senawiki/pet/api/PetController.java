package com.senawiki.pet.api;

import com.senawiki.pet.api.dto.PetResponse;
import com.senawiki.pet.service.PetService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public List<PetResponse> list() {
        return petService.list();
    }

    @GetMapping("/{id}")
    public PetResponse get(@PathVariable String id) {
        return petService.get(id);
    }
}
