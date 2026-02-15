package edu.uth.childvaccinesystem.mappers;

import edu.uth.childvaccinesystem.dtos.request.ChildRequest;
import edu.uth.childvaccinesystem.dtos.response.ChildResponse;
import edu.uth.childvaccinesystem.entities.Child;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChildMapper {
    Child toEntity(ChildRequest childRequest);
    ChildResponse toResponse(Child child);

    List<ChildResponse> toResponseList(List<Child> children);
    default Page<ChildResponse> toResponsePage(Page<Child> children) {
        return children.map(this::toResponse);
    }
}
