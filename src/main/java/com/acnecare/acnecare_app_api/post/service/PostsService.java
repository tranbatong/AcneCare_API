package com.acnecare.acnecare_app_api.post.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acnecare.acnecare_app_api.common.exception.AppException;
import com.acnecare.acnecare_app_api.common.exception.ErrorCode;
import com.acnecare.acnecare_app_api.post.repository.CommentRepository;
import com.acnecare.acnecare_app_api.post.repository.LikesRepository;
import com.acnecare.acnecare_app_api.post.repository.PostsRepository;
import com.acnecare.acnecare_app_api.post.dto.request.PostsRequest;
import com.acnecare.acnecare_app_api.post.dto.response.CommentResponse;
import com.acnecare.acnecare_app_api.post.dto.response.PostsResponse;
import com.acnecare.acnecare_app_api.post.dto.response.UserPostsResponse;
import com.acnecare.acnecare_app_api.post.entity.Comment;
import com.acnecare.acnecare_app_api.post.entity.Post;
import com.acnecare.acnecare_app_api.post.mapper.CommentMapper;
import com.acnecare.acnecare_app_api.post.mapper.PostMapper;
import com.acnecare.acnecare_app_api.identity.entity.User;
import com.acnecare.acnecare_app_api.identity.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import com.acnecare.acnecare_app_api.profile.entity.UserProfile;
import com.acnecare.acnecare_app_api.profile.repository.UserProfileRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostsService {

    PostsRepository postsRepository;
    UserRepository userRepository;
    PostMapper postsMapper;
    LikesRepository likesRepository;
    CommentRepository commentRepository;
    CommentMapper commentMapper;
    UserProfileRepository userProfileRepository;

    private void enrichPostWithLikeInfo(PostsResponse response, String postID){
        long totalLikes = likesRepository.countByPostsId(postID);
        response.setLikesCount(totalLikes);
        try {
            var userId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (userId != null) {
                boolean isLiked = likesRepository.existsByPostsIdAndUserId(postID, userId);
                response.setLiked(isLiked);
            } else {
                response.setLiked(false);
            }
        } catch (Exception e) {
            response.setLiked(false);
        }
    }

    private void enrichPostWithCommentInfo(PostsResponse response, String postId){
        List<Comment> comments = commentRepository.findByPostsIdOrderByCreateAtDesc(postId);
        
        List<CommentResponse> commentResponses = comments.stream()
                .map(commentMapper::toCommentResponse)
                .toList();
                
        response.setComments(commentResponses);
        response.setCommentsCount(commentResponses.size());
    }
    
    @Transactional(readOnly = true)
    public List<PostsResponse> getAllPosts() {
        List<Post> postsList = postsRepository.findAll();
        return postsList.stream()
                .map((post) -> {
                    PostsResponse response = postsMapper.toPostsResponse(post);
                    User user = post.getUser();
                    UserProfile userProfile = userProfileRepository.findByUserId(user.getId()).orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));
                    if (user != null) {
                        response.setUser(UserPostsResponse.builder()
                                .id(user.getId())
                                .role(postsMapper.toRoleResponseSet(user.getRoles()))
                                .name(userProfile.getFirstName() + " " + userProfile.getLastName())
                                .build());
                    }
                    enrichPostWithLikeInfo(response, post.getId());
                    enrichPostWithCommentInfo(response, post.getId());
                    return response;
                })
                .toList();
    }

    public List<PostsResponse> getPostsByUserId(String userId) {
        var isValidUser = userRepository.existsById(userId);
        if (!isValidUser) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        UserProfile userProfile = userProfileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));
        List<Post> postsList = postsRepository.findByUserId(userId);
        return postsList.stream()
                .map((post) -> {
                    PostsResponse response = postsMapper.toPostsResponse(post);
                    User user = post.getUser();
                    if (user != null) {
                        response.setUser(UserPostsResponse.builder()
                                .id(user.getId())
                                .role(postsMapper.toRoleResponseSet(user.getRoles()))
                                .name(userProfile.getFirstName() + " " + userProfile.getLastName())
                                .build());
                    }
                    enrichPostWithLikeInfo(response, post.getId());
                    enrichPostWithCommentInfo(response, post.getId());
                    return response;

                }).toList();
    }

    public PostsResponse getPostById(String postId) {
        Post post = postsRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        PostsResponse response = postsMapper.toPostsResponse(post);
        User user = post.getUser();
        UserProfile userProfile = userProfileRepository.findByUserId(user.getId()).orElseThrow(() -> new AppException(ErrorCode.USER_PROFILE_NOT_FOUND));
        if (user != null) {
            response.setUser(UserPostsResponse.builder()
                    .id(user.getId())
                    .role(postsMapper.toRoleResponseSet(user.getRoles()))
                    .name(userProfile.getFirstName() + " " + userProfile.getLastName())
                    .build());
        }
        enrichPostWithLikeInfo(response, post.getId());
        enrichPostWithCommentInfo(response, post.getId());
        return response;
    }

    public PostsResponse createPost(String userId, PostsRequest request){
        var isValidUser = userRepository.existsById(userId);
        if (!isValidUser){
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        Post posts = postsMapper.toPosts(request);
        posts.setUser(userRepository.getReferenceById(userId));
        posts.setCreatedAt(LocalDateTime.now());
        posts.setUpdatedAt(LocalDateTime.now());
        posts.setStatus(request.getStatus());

        return postsMapper.toPostsResponse(postsRepository.save(posts));
    }

    public PostsResponse updatePost(String userId, String postId, PostsRequest request) {
        var isValidUser = userRepository.existsById(userId);
        if (!isValidUser) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Post posts = postsRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if(userId != null && !posts.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        posts.setPostTitle(request.getPostTitle());
        posts.setPostContent(request.getPostContent());
        posts.setStatus(request.getStatus());
        posts.setUpdatedAt(LocalDateTime.now());

        return postsMapper.toPostsResponse(postsRepository.save(posts));
    }

    public void deletePost(String userId, String postId) {
        Post posts = postsRepository.findById(postId).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        if (userId != null && !posts.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        postsRepository.delete(posts);
    }
}
