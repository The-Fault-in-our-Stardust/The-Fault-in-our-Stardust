package com.zipcode.stardust.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.zipcode.stardust.model.Bird;
import com.zipcode.stardust.model.Species;
import com.zipcode.stardust.repository.BirdRepository;
import com.zipcode.stardust.repository.SpeciesRepository;

@Service
public class EBirdService {

    private static final String TAXONOMY_URL =
        "https://api.ebird.org/v2/ref/taxonomy/ebird?fmt=json&cat=species";

    private static final List<String> FEATURED_NORTH_AMERICAN_BIRDS = List.of(
        "Northern Cardinal",
        "American Robin",
        "Blue Jay",
        "American Crow",
        "Mourning Dove",
        "House Finch",
        "Black-capped Chickadee",
        "Tufted Titmouse",
        "Red-bellied Woodpecker",
        "Downy Woodpecker",
        "White-breasted Nuthatch",
        "Carolina Wren",
        "Song Sparrow",
        "House Sparrow",
        "European Starling",
        "Northern Mockingbird",
        "Common Grackle",
        "Red-winged Blackbird",
        "Mallard",
        "Canada Goose"
    );

    @Value("${ebird.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private final BirdRepository birdRepository;
    private final SpeciesRepository speciesRepository;

    public EBirdService(
            BirdRepository birdRepository,
            SpeciesRepository speciesRepository) {

        this.birdRepository = birdRepository;
        this.speciesRepository = speciesRepository;
    }

    // ========================================
    // GET FULL EBIRD TAXONOMY
    // ========================================

    private List<EBirdBird> getAllBirds() {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-eBirdApiToken", apiKey);

        HttpEntity<Void> request =
            new HttpEntity<>(headers);

        ResponseEntity<EBirdBird[]> response =
            restTemplate.exchange(
                TAXONOMY_URL,
                HttpMethod.GET,
                request,
                EBirdBird[].class
            );

        EBirdBird[] birds = response.getBody();

        if (birds == null) {
            return List.of();
        }

        return Arrays.asList(birds);
    }

    // ========================================
    // BIRD DIRECTORY / SEARCH
    // ========================================

    public List<EBirdBird> getBirds(String search) {

        List<EBirdBird> birdList = getAllBirds();

        // Search the full eBird taxonomy.
        if (search != null && !search.isBlank()) {

            String searchLower =
                search.trim().toLowerCase();

            return birdList.stream()
                .filter(bird ->
                    bird.getComName() != null
                    &&
                    bird.getSciName() != null
                    &&
                    (
                        bird.getComName()
                            .toLowerCase()
                            .contains(searchLower)
                        ||
                        bird.getSciName()
                            .toLowerCase()
                            .contains(searchLower)
                    )
                )
                .limit(20)
                .toList();
        }

        // No search:
        // show our featured North American birds.
        return FEATURED_NORTH_AMERICAN_BIRDS.stream()
            .map(featuredName ->
                birdList.stream()
                    .filter(bird ->
                        bird.getComName() != null
                        &&
                        bird.getComName()
                            .equalsIgnoreCase(featuredName)
                    )
                    .findFirst()
                    .orElse(null)
            )
            .filter(bird -> bird != null)
            .toList();
    }

    // ========================================
    // FIND ONE EBIRD BIRD
    // ========================================

    public EBirdBird getBirdByCode(String speciesCode) {

        return getAllBirds().stream()
            .filter(bird ->
                bird.getSpeciesCode() != null
                &&
                bird.getSpeciesCode()
                    .equalsIgnoreCase(speciesCode)
            )
            .findFirst()
            .orElse(null);
    }

    // ========================================
    // GET SPECIES CODES FOR A REGION
    // ========================================

    public List<String> getSpeciesCodesByRegion(
            String regionCode) {

        String url =
            "https://api.ebird.org/v2/product/spplist/"
            + regionCode;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-eBirdApiToken", apiKey);

        HttpEntity<Void> request =
            new HttpEntity<>(headers);

        ResponseEntity<String[]> response =
            restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String[].class
            );

        String[] speciesCodes = response.getBody();

        if (speciesCodes == null) {
            return List.of();
        }

        return Arrays.asList(speciesCodes);
    }

    // ========================================
    // GET BIRDS REPORTED IN A REGION
    // ========================================

    public List<EBirdBird> getBirdsByRegion(
            String regionCode) {

        List<String> speciesCodes =
            getSpeciesCodesByRegion(regionCode);

        List<EBirdBird> allBirds =
            getAllBirds();

        return allBirds.stream()
            .filter(bird ->
                bird.getSpeciesCode() != null
                &&
                speciesCodes.contains(
                    bird.getSpeciesCode()
                )
            )
            .toList();
    }

    // ========================================
    // IMPORT EBIRD DATA INTO DATABASE
    // ========================================

    public int importBirds(List<EBirdBird> eBirdBirds) {

        int importedCount = 0;

        for (EBirdBird eBirdBird : eBirdBirds) {

            // Skip records without a common name.
            if (eBirdBird.getComName() == null
                    || eBirdBird.getComName().isBlank()) {

                continue;
            }

            // Check whether this bird already exists.
            Bird existingBird =
                birdRepository.findByNameIgnoreCase(
                    eBirdBird.getComName()
                );

            // If it already exists, don't save it again.
            if (existingBird != null) {
                continue;
            }

            /*
             * Use eBird's scientific family name
             * for our Species record.
             *
             * Example:
             * Cardinalidae
             */
            String speciesName =
                eBirdBird.getFamilySciName();

            if (speciesName == null
                    || speciesName.isBlank()) {

                speciesName = "Unknown";
            }

            /*
             * Use eBird's common family name
             * as the Species description.
             *
             * Example:
             * Cardinals and Allies
             */
            String speciesDescription =
                eBirdBird.getFamilyComName();

            /*
             * First try to find the Species.
             *
             * If it doesn't exist, create it.
             */
            Species species =
                speciesRepository
                    .findByNameIgnoreCase(speciesName)
                    .orElse(null);

            if (species == null) {

                species = new Species(
                    speciesName,
                    speciesDescription
                );

                species =
                    speciesRepository.save(species);
            }

            /*
             * Create our database Bird.
             *
             * eBird taxonomy doesn't provide a
             * full "about" description, so we're
             * leaving about null for now.
             */
            Bird bird = new Bird(
                eBirdBird.getComName(),
                null,
                species
            );

            birdRepository.save(bird);

            importedCount++;
        }

        return importedCount;
    }
}