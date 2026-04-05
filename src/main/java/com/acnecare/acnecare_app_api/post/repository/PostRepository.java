package com.acnecare.acnecare_app_api.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.acnecare.acnecare_app_api.post.entity.Post;

public interface PostRepository extends JpaRepository<Post, String> {
}
