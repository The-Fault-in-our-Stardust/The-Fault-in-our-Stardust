package com.zipcode.stardust.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zipcode.stardust.model.Post;
import com.zipcode.stardust.model.Reaction;
import com.zipcode.stardust.model.ReactionType;
import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.PostRepository;
import com.zipcode.stardust.repository.ReactionRepository;
import com.zipcode.stardust.repository.UserRepository;

@Controller
public class ReactionController {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ReactionController(
            ReactionRepository reactionRepository,
            PostRepository postRepository,
            UserRepository userRepository) {

        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/posts/{postId}/react")
    public String reactToPost(
            @PathVariable Long postId,
            @RequestParam ReactionType type,
            Authentication authentication) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        Post post = postRepository
                .findById(postId)
                .orElseThrow();

        Reaction reaction = new Reaction(type, user, post, null);

        reactionRepository.save(reaction);

        return "redirect:/";
    }
}