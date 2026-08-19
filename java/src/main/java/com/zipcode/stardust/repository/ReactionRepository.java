package com.zipcode.stardust.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipcode.stardust.model.Reaction;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
}