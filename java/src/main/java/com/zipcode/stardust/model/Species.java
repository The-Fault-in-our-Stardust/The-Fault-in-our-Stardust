package com.zipcode.stardust.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "species")
public class Species {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(length = 4000)
    private String description;

    @OneToMany(mappedBy = "species", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Bird> birds = new ArrayList<>();

    public Species() {}

    public Species(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Bird> getBirds() { return birds; }
    public void setBirds(List<Bird> birds) { this.birds = birds; }
}