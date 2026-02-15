package edu.uth.childvaccinesystem.mappers;

import edu.uth.childvaccinesystem.dtos.request.VaccineRequest;
import edu.uth.childvaccinesystem.dtos.response.VaccineResponse;
import edu.uth.childvaccinesystem.entities.Vaccine;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VaccineMapper {
    Vaccine toEntity(VaccineRequest vaccineRequest);
    VaccineResponse toResponse(Vaccine vaccine);
    List<VaccineResponse> toResponseList(List<Vaccine> vaccines);
    default Page<VaccineResponse> toResponsePage(Page<Vaccine> vaccines) {
        return vaccines.map(this::toResponse);
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVaccineFromRequest(Vaccine vaccine1, Vaccine vaccine2);
}
