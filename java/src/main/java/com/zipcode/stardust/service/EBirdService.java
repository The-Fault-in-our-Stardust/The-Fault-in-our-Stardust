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

    public List<EBirdBird> getBirds(String search) {

        List<EBirdBird> birdList = getAllBirds();

        // If someone searches, search the full eBird taxonomy.
        if (search != null && !search.isBlank()) {

            String searchLower = search.toLowerCase();

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

        // If there is no search, show featured North American birds.
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
    public List<String> getSpeciesCodesByRegion(String regionCode) {

    String url =
        "https://api.ebird.org/v2/product/spplist/" + regionCode;

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
    public List<EBirdBird> getBirdsByRegion(String regionCode) {

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
}