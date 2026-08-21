package com.zipcode.stardust.repository;

import com.zipcode.stardust.model.Bird;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BirdRepository extends JpaRepository<Bird, Long> {
    List<Bird> findBySpeciesId(Long speciesId);
}