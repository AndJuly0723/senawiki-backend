package com.senawiki.hero.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeroRepository extends JpaRepository<Hero, String> {

    Optional<Hero> findByName(String name);
}
