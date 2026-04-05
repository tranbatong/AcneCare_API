package com.acnecare.acnecare_app_api.post.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.acnecare.acnecare_app_api.common.exception.AppException;
import com.acnecare.acnecare_app_api.common.exception.ErrorCode;
import com.acnecare.acnecare_app_api.post.repository.CommentRepository;
import com.acnecare.acnecare_app_api.post.repository.PostsRepository;
import com.acnecare.acnecare_app_api.post.dto.request.CommentRequest;
import com.acnecare.acnecare_app_api.post.dto.response.CommentResponse;
import com.acnecare.acnecare_app_api.post.entity.Comment;
import com.acnecare.acnecare_app_api.post.entity.Post;
import com.acnecare.acnecare_app_api.post.mapper.CommentMapper;
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
public class CommentService {
    CommentRepository commentRepository;
    PostsRepository postsRepository;
    UserRepository userRepository;
    CommentMapper commentMapper;

    public List<CommentResponse> getAllComments(String postId){
        if (!postsRepository.existsById(postId)) {
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        }
        List<Comment> comments = commentRepository.findByPostsIdOrderByCreateAtDesc(postId);

        return comments.stream()
                        .map(commentMapper::toCommentResponse)
                        .toList();
    }
    
    public CommentResponse createComment(String postId, CommentRequest request){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId)
            .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        Post posts = postsRepository.findById(postId)
            .orElseThrow(()-> new AppException(ErrorCode.POST_NOT_FOUND));
        Comment comment = Comment.builder()
            .commentContent(request.getCommentContent())
            .user(user)
            .posts(posts)
            .createAt(LocalDateTime.now())
            .build();
        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    public CommentResponse updateComment(String id, CommentRequest request){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Comment comment = commentRepository.findById(id)
            .orElseThrow(()-> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        comment.setCommentContent(request.getCommentContent());
        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    public void deleteComment(String id){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Comment comment = commentRepository.findById(id)
            .orElseThrow(()-> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        if(!comment.getUser().getId().equals(userId)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        commentRepository.delete(comment);
    }
}
