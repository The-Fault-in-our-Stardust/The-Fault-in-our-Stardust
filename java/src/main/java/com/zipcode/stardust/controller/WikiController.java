package com.zipcode.stardust.controller;



import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.zipcode.stardust.model.Species;
import com.zipcode.stardust.model.Bird;

import com.zipcode.stardust.repository.SpeciesRepository;
import com.zipcode.stardust.repository.BirdRepository;

@Controller
@RequestMapping("/wiki")
public class WikiController {

    private final BirdRepository birdRepository;
    private final SpeciesRepository speciesRepository;

    public WikiController(BirdRepository birdRepository, SpeciesRepository speciesRepository) {
        this.birdRepository = birdRepository;
        this.speciesRepository = speciesRepository;
    }

   @GetMapping("/species")
        public String getSpeciesDirectory(Model model) {
            List<Species> allSpecies = speciesRepository.findAll();
            model.addAttribute("title", "Species");
            model.addAttribute("urlSegment", "species");
            model.addAttribute("itemType", "species");
            model.addAttribute("items", allSpecies);
            return "directory";
        }

@GetMapping("/bird")
    public String getBirdDirectory(Model model) {
        List<Bird> allBirds = birdRepository.findAll();
        model.addAttribute("title", "Birds");
        model.addAttribute("urlSegment", "bird");
        model.addAttribute("itemType", "bird");
        model.addAttribute("items", allBirds);
        return "directory";
    }


    @GetMapping("/bird/{id}")
    public String getBird(@PathVariable Long id, Model model) {
        Bird bird = birdRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("name", bird.getName());
        model.addAttribute("subtitle", bird.getSpecies().getName()); // "species" line
        model.addAttribute("about", bird.getAbout());
        return "wiki";
    }

    @GetMapping("/species/{id}")
    public String getSpecies(@PathVariable Long id, Model model) {
        Species species = speciesRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("name", species.getName());
        model.addAttribute("subtitle", "Species");
        model.addAttribute("about", species.getDescription());
        return "wiki";
    }
}