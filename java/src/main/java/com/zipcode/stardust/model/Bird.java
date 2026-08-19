package com.zipcode.stardust.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bird")
public class Bird {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 4000)
    private String about;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;

    public Bird() {}

    public Bird(String name, String about, Species species) {
        this.name = name;
        this.about = about;
        this.species = species;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }
    public Species getSpecies() { return species; }
    public void setSpecies(Species species) { this.species = species; }
}