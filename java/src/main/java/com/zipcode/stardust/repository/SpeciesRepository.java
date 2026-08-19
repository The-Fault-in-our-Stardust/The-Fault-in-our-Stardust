package com.zipcode.stardust.repository;

import com.zipcode.stardust.model.Species;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeciesRepository extends JpaRepository<Species, Long> {
}