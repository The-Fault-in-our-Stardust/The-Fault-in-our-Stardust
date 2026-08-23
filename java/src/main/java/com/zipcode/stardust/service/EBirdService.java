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

        List<EBirdBird> birdList =
            getAllBirds();

        if (search != null && !search.isBlank()) {

            String searchLower =
                search.toLowerCase();

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

        return birdList.stream()
            .limit(20)
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
}