package com.zipcode.stardust.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.zipcode.stardust.model.Bird;
import com.zipcode.stardust.model.Species;
import com.zipcode.stardust.repository.BirdRepository;
import com.zipcode.stardust.repository.SpeciesRepository;
import com.zipcode.stardust.service.EBirdBird;
import com.zipcode.stardust.service.EBirdService;

@Controller
@RequestMapping("/wiki")
public class WikiController {

    private final BirdRepository birdRepository;
    private final SpeciesRepository speciesRepository;
    private final EBirdService eBirdService;

    /*
     * Maps the state name typed by the user
     * to the region code expected by eBird.
     *
     * Example:
     * Delaware -> US-DE
     * Maryland -> US-MD
     */
    private static final Map<String, String> STATE_CODES = Map.ofEntries(

        Map.entry("alabama", "US-AL"),
        Map.entry("alaska", "US-AK"),
        Map.entry("arizona", "US-AZ"),
        Map.entry("arkansas", "US-AR"),
        Map.entry("california", "US-CA"),
        Map.entry("colorado", "US-CO"),
        Map.entry("connecticut", "US-CT"),
        Map.entry("delaware", "US-DE"),
        Map.entry("florida", "US-FL"),
        Map.entry("georgia", "US-GA"),
        Map.entry("hawaii", "US-HI"),
        Map.entry("idaho", "US-ID"),
        Map.entry("illinois", "US-IL"),
        Map.entry("indiana", "US-IN"),
        Map.entry("iowa", "US-IA"),
        Map.entry("kansas", "US-KS"),
        Map.entry("kentucky", "US-KY"),
        Map.entry("louisiana", "US-LA"),
        Map.entry("maine", "US-ME"),
        Map.entry("maryland", "US-MD"),
        Map.entry("massachusetts", "US-MA"),
        Map.entry("michigan", "US-MI"),
        Map.entry("minnesota", "US-MN"),
        Map.entry("mississippi", "US-MS"),
        Map.entry("missouri", "US-MO"),
        Map.entry("montana", "US-MT"),
        Map.entry("nebraska", "US-NE"),
        Map.entry("nevada", "US-NV"),
        Map.entry("new hampshire", "US-NH"),
        Map.entry("new jersey", "US-NJ"),
        Map.entry("new mexico", "US-NM"),
        Map.entry("new york", "US-NY"),
        Map.entry("north carolina", "US-NC"),
        Map.entry("north dakota", "US-ND"),
        Map.entry("ohio", "US-OH"),
        Map.entry("oklahoma", "US-OK"),
        Map.entry("oregon", "US-OR"),
        Map.entry("pennsylvania", "US-PA"),
        Map.entry("rhode island", "US-RI"),
        Map.entry("south carolina", "US-SC"),
        Map.entry("south dakota", "US-SD"),
        Map.entry("tennessee", "US-TN"),
        Map.entry("texas", "US-TX"),
        Map.entry("utah", "US-UT"),
        Map.entry("vermont", "US-VT"),
        Map.entry("virginia", "US-VA"),
        Map.entry("washington", "US-WA"),
        Map.entry("west virginia", "US-WV"),
        Map.entry("wisconsin", "US-WI"),
        Map.entry("wyoming", "US-WY")
    );

    public WikiController(
            BirdRepository birdRepository,
            SpeciesRepository speciesRepository,
            EBirdService eBirdService) {

        this.birdRepository = birdRepository;
        this.speciesRepository = speciesRepository;
        this.eBirdService = eBirdService;
    }

    // ========================================
    // SPECIES DIRECTORY
    // ========================================

    @GetMapping("/species")
    public String getSpeciesDirectory(
            @RequestParam(required = false) String location,
            Model model) {

        List<EBirdBird> species = List.of();

        /*
         * Only search eBird when the user actually
         * entered something into the location box.
         */
        if (location != null && !location.isBlank()) {

            /*
             * trim() removes accidental spaces.
             *
             * toLowerCase() lets "Delaware",
             * "delaware", and "DELAWARE" all work.
             */
            String normalizedLocation =
                location.trim().toLowerCase();

            String regionCode =
                STATE_CODES.get(normalizedLocation);

            /*
             * If the state exists in our map,
             * send its eBird region code to the service.
             */
            if (regionCode != null) {
                species =
                    eBirdService.getBirdsByRegion(regionCode);
            }
        }

        model.addAttribute("title", "Species");
        model.addAttribute("itemType", "especies");
        model.addAttribute("items", species);
        model.addAttribute("location", location);

        return "directory";
    }

    // ========================================
    // BIRD DIRECTORY
    // ========================================

    @GetMapping("/bird")
    public String getBirdDirectory(
            @RequestParam(required = false) String search,
            Model model) {

        List<EBirdBird> birds =
            eBirdService.getBirds(search);

        model.addAttribute("title", "Birds");
        model.addAttribute("urlSegment", "bird");
        model.addAttribute("itemType", "ebird");
        model.addAttribute("items", birds);
        model.addAttribute("search", search);

        return "directory";
    }

    // ========================================
    // DATABASE BIRD PAGE
    // ========================================

    @GetMapping("/bird/{id}")
    public String getBird(
            @PathVariable Long id,
            Model model) {

        Bird bird = birdRepository.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND
                )
            );

        model.addAttribute("name", bird.getName());
        model.addAttribute(
            "subtitle",
            bird.getSpecies().getName()
        );
        model.addAttribute("about", bird.getAbout());

        return "wiki";
    }

    // ========================================
    // DATABASE SPECIES PAGE
    // ========================================

    @GetMapping("/species/{id}")
    public String getSpecies(
            @PathVariable Long id,
            Model model) {

        Species species = speciesRepository.findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND
                )
            );

        model.addAttribute("name", species.getName());
        model.addAttribute("subtitle", "Species");
        model.addAttribute(
            "about",
            species.getDescription()
        );

        return "wiki";
    }
}