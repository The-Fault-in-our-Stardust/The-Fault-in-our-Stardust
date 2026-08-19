package com.zipcode.stardust.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipcode.stardust.model.Comment;
import com.zipcode.stardust.model.Post;
import com.zipcode.stardust.model.Reaction;
import com.zipcode.stardust.model.ReactionType;
import com.zipcode.stardust.model.User;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    // Count reactions on a post
    long countByPostAndType(
            Post post,
            ReactionType type
    );

    // Count reactions on a comment
    long countByCommentAndType(
            Comment comment,
            ReactionType type
    );

    // Find this user's existing reaction on a post
    Optional<Reaction> findByUserAndPost(
            User user,
            Post post
    );

    // Find this user's existing reaction on a comment
    Optional<Reaction> findByUserAndComment(
            User user,
            Comment comment
    );
}