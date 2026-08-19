package com.zipcode.stardust.config;

import com.zipcode.stardust.model.Bird;
import com.zipcode.stardust.model.Species;
import com.zipcode.stardust.model.Subforum;
import com.zipcode.stardust.repository.BirdRepository;
import com.zipcode.stardust.repository.SpeciesRepository;
import com.zipcode.stardust.repository.SubforumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private SubforumRepository subforumRepository;

     @Autowired
    private SpeciesRepository speciesRepository;

     @Autowired
    private BirdRepository birdRepository;


    @Override
    public void run(ApplicationArguments args) {
        if (subforumRepository.count() == 0) {
            Subforum forum = new Subforum("Forum",
                    "Announcements, bug reports, and general discussion about the forum belongs here", null);
            subforumRepository.save(forum);

            Subforum announcements = new Subforum("Announcements",
                    "View forum announcements here", forum);
            subforumRepository.save(announcements);

            Subforum bugReports = new Subforum("Bug Reports",
                    "Report bugs with the forum here", forum);
            subforumRepository.save(bugReports);

            Subforum general = new Subforum("General Discussion",
                    "Use this subforum to post anything you want", null);
            subforumRepository.save(general);

            Subforum other = new Subforum("Other",
                    "Discuss other things here", null);
            subforumRepository.save(other);
        }

        if (speciesRepository.count() == 0) {
            Species cardinalSpecies = new Species("Cardinalidae",
                "A family of passerine birds found in North and South America.");
            speciesRepository.save(cardinalSpecies);

            Species corvidSpecies = new Species("Corvidae",
                "A family of birds that includes crows, ravens, and jays.");
            speciesRepository.save(corvidSpecies);

            Bird cardinal = new Bird("Northern Cardinal",
                "A mid-sized songbird with a distinctive crest and, in males, vivid red plumage.",
                cardinalSpecies);
            birdRepository.save(cardinal);

            Bird crow = new Bird("American Crow",
                "A large, all-black bird known for its intelligence and adaptability.",
                corvidSpecies);
            birdRepository.save(crow);
        }
    }
}
