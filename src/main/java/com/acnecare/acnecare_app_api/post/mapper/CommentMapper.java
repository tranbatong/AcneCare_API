package com.acnecare.acnecare_app_api.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.acnecare.acnecare_app_api.post.dto.response.CommentResponse;
import com.acnecare.acnecare_app_api.post.entity.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "firstName", expression = "java(comment.getUser() != null ? comment.getUser().getEmail() : null)")
    @Mapping(target = "lastName", constant = "")
    @Mapping(target = "avatarUrl", constant = "")
    CommentResponse toCommentResponse(Comment comment);
}
