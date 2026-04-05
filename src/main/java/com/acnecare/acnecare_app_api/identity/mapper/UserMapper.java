package com.acnecare.acnecare_app_api.identity.mapper;

import org.mapstruct.Mapper;
import com.acnecare.acnecare_app_api.identity.dto.request.UserCreationRequest;
import com.acnecare.acnecare_app_api.identity.entity.User;
import com.acnecare.acnecare_app_api.identity.dto.response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

}
