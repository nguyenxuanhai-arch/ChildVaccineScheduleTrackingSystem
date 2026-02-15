package edu.uth.childvaccinesystem.mappers;

import edu.uth.childvaccinesystem.dtos.response.UserProfileResponse;
import edu.uth.childvaccinesystem.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserProfileResponse toResponse(User user);
}
