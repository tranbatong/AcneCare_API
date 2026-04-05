package com.acnecare.acnecare_app_api.post.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.acnecare.acnecare_app_api.admin.dto.request.AdminPostCreationRequest;
import com.acnecare.acnecare_app_api.admin.dto.request.AdminPostUpdateRequest;
import com.acnecare.acnecare_app_api.admin.dto.response.AdminPostResponse;
import com.acnecare.acnecare_app_api.post.entity.Post;
import com.acnecare.acnecare_app_api.post.mapper.PostMapper;
import com.acnecare.acnecare_app_api.post.repository.PostRepository;
import com.acnecare.acnecare_app_api.identity.repository.UserRepository;
import com.acnecare.acnecare_app_api.common.exception.AppException;
import com.acnecare.acnecare_app_api.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {

    PostRepository postRepository;
    PostMapper postMapper;
    UserRepository userRepository;

    public List<AdminPostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(postMapper::toAdminPostResponse)
                .toList();
    }

    public AdminPostResponse getPostById(String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        return postMapper.toAdminPostResponse(post);
    }

    @Transactional
    public AdminPostResponse createPost(AdminPostCreationRequest request) {
        if (request.getAuthorId() != null && !userRepository.existsById(request.getAuthorId())) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Post post = postMapper.toPost(request);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        Post saved = postRepository.save(post);
        return postMapper.toAdminPostResponse(saved);
    }

    @Transactional
    public AdminPostResponse updatePost(String id, AdminPostUpdateRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        postMapper.updatePostFromRequest(request, post);
        post.setUpdatedAt(LocalDateTime.now());
        Post saved = postRepository.save(post);
        return postMapper.toAdminPostResponse(saved);
    }

    @Transactional
    public void deletePost(String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        postRepository.delete(post);
    }
}
