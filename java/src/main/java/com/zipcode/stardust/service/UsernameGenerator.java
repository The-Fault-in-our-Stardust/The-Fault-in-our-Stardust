package com.zipcode.stardust.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.zipcode.stardust.repository.UserRepository;

@Service
public class UsernameGenerator {

    private final UserRepository userRepository;

    private static final String[] ADJECTIVES = {
        "Happy",
        "Swift",
        "Clever",
        "Golden",
        "Little",
        "Brave",
        "Curious"
    };

    private static final String[] BIRDS = {
        "Robin",
        "BlueJay",
        "Finch",
        "Falcon",
        "Sparrow",
        "Cardinal",
        "Owl"
    };

    private static final Random RANDOM = new Random();

    public UsernameGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateUsername() {
        String username;

        do {
            String adjective = ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)];
            String bird = BIRDS[RANDOM.nextInt(BIRDS.length)];
            int number = RANDOM.nextInt(100);

            username = adjective + bird + number;

        } while (userRepository.existsByUsername(username));

        return username;
    }
}