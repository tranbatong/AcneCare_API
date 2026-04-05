package com.acnecare.acnecare_app_api.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.acnecare.acnecare_app_api.post.entity.Post;

@Repository
public interface PostsRepository extends JpaRepository<Post, String> {
    List<Post> findByUserId(String userId);
}
