package com.acnecare.acnecare_app_api.post.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.acnecare.acnecare_app_api.post.dto.request.PostsRequest;
import com.acnecare.acnecare_app_api.post.dto.response.PostsResponse;
import com.acnecare.acnecare_app_api.admin.dto.request.AdminPostCreationRequest;
import com.acnecare.acnecare_app_api.admin.dto.request.AdminPostUpdateRequest;
import com.acnecare.acnecare_app_api.admin.dto.response.AdminPostResponse;
import com.acnecare.acnecare_app_api.post.entity.Post;
import com.acnecare.acnecare_app_api.identity.dto.response.RoleResponse;
import com.acnecare.acnecare_app_api.identity.entity.Role;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toPosts(PostsRequest request);    
    PostsResponse toPostsResponse(Post posts);
    
    @Mapping(target = "user", ignore = true)
    List<PostsResponse> toPostsResponseList(List<Post> postsList);
    Set<RoleResponse> toRoleResponseSet(Set<Role> roles);

    @Mapping(target = "postTitle", source = "title")
    @Mapping(target = "postContent", source = "content")
    @Mapping(target = "user.id", source = "authorId")
    Post toPost(AdminPostCreationRequest request);

    @Mapping(target = "title", source = "postTitle")
    @Mapping(target = "content", source = "postContent")
    @Mapping(target = "authorId", source = "user.id")
    AdminPostResponse toAdminPostResponse(Post post);

    @Mapping(target = "postTitle", source = "title")
    @Mapping(target = "postContent", source = "content")
    void updatePostFromRequest(AdminPostUpdateRequest request, @MappingTarget Post post);
}
