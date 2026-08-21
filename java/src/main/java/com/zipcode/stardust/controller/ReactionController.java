package com.zipcode.stardust.controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zipcode.stardust.model.Comment;
import com.zipcode.stardust.model.Post;
import com.zipcode.stardust.model.Reaction;
import com.zipcode.stardust.model.ReactionType;
import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.CommentRepository;
import com.zipcode.stardust.repository.PostRepository;
import com.zipcode.stardust.repository.ReactionRepository;
import com.zipcode.stardust.repository.UserRepository;

@Controller
public class ReactionController {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public ReactionController(
            ReactionRepository reactionRepository,
            PostRepository postRepository,
            UserRepository userRepository,
            CommentRepository commentRepository) {

        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
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

        Optional<Reaction> existingReaction =
                reactionRepository.findByUserAndPost(user, post);

        if (existingReaction.isPresent()) {

            Reaction reaction = existingReaction.get();

            if (reaction.getType() == type) {
                // Same reaction clicked again:
                // remove the reaction.
                reactionRepository.delete(reaction);

            } else {
                // Different reaction clicked:
                // change the existing reaction.
                reaction.setType(type);
                reactionRepository.save(reaction);
            }

        } else {

            // User has not reacted yet:
            // create a new reaction.
            Reaction reaction = new Reaction(
                    type,
                    user,
                    post,
                    null
            );

            reactionRepository.save(reaction);
        }

        return "redirect:/viewpost?post=" + postId;
    }

    @PostMapping("/comments/{commentId}/react")
    public String reactToComment(
            @PathVariable Long commentId,
            @RequestParam ReactionType type,
            Authentication authentication) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow();

        Optional<Reaction> existingReaction =
                reactionRepository.findByUserAndComment(user, comment);

        if (existingReaction.isPresent()) {

            Reaction reaction = existingReaction.get();

            if (reaction.getType() == type) {
                // Same reaction clicked again:
                // remove the reaction.
                reactionRepository.delete(reaction);

            } else {
                // Different reaction clicked:
                // change the existing reaction.
                reaction.setType(type);
                reactionRepository.save(reaction);
            }

        } else {

            // User has not reacted yet:
            // create a new reaction.
            Reaction reaction = new Reaction(
                    type,
                    user,
                    null,
                    comment
            );

            reactionRepository.save(reaction);
        }

        return "redirect:/viewpost?post="
                + comment.getPost().getId();
    }
}