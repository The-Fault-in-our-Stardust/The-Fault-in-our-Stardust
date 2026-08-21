package com.zipcode.stardust.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipcode.stardust.model.Post;
import com.zipcode.stardust.model.Subforum;

public interface PostRepository extends JpaRepository<Post, Long> {

    // List<Post> findBySubforumOrderByPostdateDesc(Subforum subforum);

    // List<Post> findAllByOrderByPostdateDesc();

    List<Post> findBySubforumAndDeletedFalseOrderByPostdateDesc(Subforum subforum);

    List<Post> findByDeletedFalseOrderByPostdateDesc(); 
}