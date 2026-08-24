package com.zipcode.stardust.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipcode.stardust.model.Bird;

public interface BirdRepository extends JpaRepository<Bird, Long> {
    List<Bird> findBySpeciesId(Long speciesId);
        
    Bird findByNameIgnoreCase(String name);

}