package com.zipcode.stardust.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipcode.stardust.model.Species;

public interface SpeciesRepository extends JpaRepository<Species, Long> {

    Optional<Species> findByNameIgnoreCase(String name);
}