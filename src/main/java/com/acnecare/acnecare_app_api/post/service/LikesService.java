package com.acnecare.acnecare_app_api.post.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.acnecare.acnecare_app_api.common.exception.AppException;
import com.acnecare.acnecare_app_api.common.exception.ErrorCode;
import com.acnecare.acnecare_app_api.post.repository.LikesRepository;
import com.acnecare.acnecare_app_api.post.repository.PostsRepository;
import com.acnecare.acnecare_app_api.post.entity.Likes;
import com.acnecare.acnecare_app_api.post.entity.Post;
import com.acnecare.acnecare_app_api.identity.entity.User;
import com.acnecare.acnecare_app_api.identity.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LikesService {
    LikesRepository likesRepository;
    PostsRepository postsRepository;
    UserRepository userRepository;

    public boolean toggleLike(String postId){

        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId)
            .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
    
        Post post = postsRepository.findById(postId)
            .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        boolean alreadyLiked = likesRepository.existsByPostsIdAndUserId(postId, userId);

        if (alreadyLiked) {
            Likes like = likesRepository.findByPostsIdAndUserId(postId, userId)
            .orElseThrow(() -> new AppException(ErrorCode.LIKE_NOT_FOUND));
            likesRepository.delete(like);
            return false;
        }

        Likes like = Likes.builder()
            .createAt(LocalDateTime.now())
            .user(user)
            .posts(post)
            .build();
        likesRepository.save(like);
        return true;
    }

    public long countLikesByPostId(String postId){
        return likesRepository.countByPostsId(postId);
    }
}
