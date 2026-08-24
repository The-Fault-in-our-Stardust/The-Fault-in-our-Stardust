package com.zipcode.stardust;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;

import com.zipcode.stardust.controller.WikiController;
import com.zipcode.stardust.repository.BirdRepository;
import com.zipcode.stardust.repository.SpeciesRepository;
import com.zipcode.stardust.service.EBirdService;

public class WikiControllerTest {

    private BirdRepository birdRepository;
    private SpeciesRepository speciesRepository;
    private EBirdService eBirdService;
    private WikiController wikiController;
    private Model model;

    @BeforeEach
    void setUp() {

        birdRepository = Mockito.mock(BirdRepository.class);
        speciesRepository = Mockito.mock(SpeciesRepository.class);
        eBirdService = Mockito.mock(EBirdService.class);
        model = Mockito.mock(Model.class);

        wikiController = new WikiController(
                birdRepository,
                speciesRepository,
                eBirdService
        );
    }

    @Test
    void birdDirectoryReturnsDirectoryPage() {

        String result = wikiController.getBirdDirectory(
                null,
                model
        );

        assertEquals("directory", result);
    }
    @Test
    void speciesDirectoryReturnsDirectoryPage() {

    String result = wikiController.getSpeciesDirectory(
            null,
            model
    );

    assertEquals("directory", result);
    }
@Test
void delawareSpeciesSearchUsesCorrectRegionCode() {

    wikiController.getSpeciesDirectory(
            "Delaware",
            model
    );

    Mockito.verify(eBirdService)
        .getBirdsByRegion("US-DE");
    }   
    @Test
void marylandSpeciesSearchUsesCorrectRegionCode() {

    wikiController.getSpeciesDirectory(
            "Maryland",
            model
    );

    Mockito.verify(eBirdService)
        .getBirdsByRegion("US-MD");
    }
    @Test
void birdDirectoryAddsExpectedModelAttributes() {

    wikiController.getBirdDirectory(
            null,
            model
    );

    Mockito.verify(model)
        .addAttribute("title", "Birds");

    Mockito.verify(model)
        .addAttribute("itemType", "ebird");

    Mockito.verify(model)
        .addAttribute("search", null);
    }
    @Test
void speciesDirectoryAddsExpectedModelAttributes() {

    wikiController.getSpeciesDirectory(
            "Delaware",
            model
    );

    Mockito.verify(model)
        .addAttribute("title", "Species");

    Mockito.verify(model)
        .addAttribute("itemType", "especies");

    Mockito.verify(model)
        .addAttribute("location", "Delaware");
    }
}