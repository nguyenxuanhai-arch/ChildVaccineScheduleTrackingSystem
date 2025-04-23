package edu.uth.childvaccinesystem.utils;

import edu.uth.childvaccinesystem.dtos.request.ChildRequestDTO;
import edu.uth.childvaccinesystem.dtos.response.ChildResponseDTO;
import edu.uth.childvaccinesystem.entities.Child;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

public class ChildMapper {
    
    public static ChildResponseDTO toDTO(Child child) {
        if (child == null) return null;
        
        ChildResponseDTO dto = new ChildResponseDTO();
        dto.setId(child.getId());
        dto.setName(child.getName());
        dto.setDob(child.getDob());
        dto.setGender(child.getGender());
        
        // Calculate age in months
        if (child.getDob() != null) {
            LocalDate now = LocalDate.now();
            Period period = Period.between(child.getDob(), now);
            int months = period.getYears() * 12 + period.getMonths();
            dto.setAgeInMonths(months);
        }
        
        return dto;
    }
    
    public static List<ChildResponseDTO> toDTOList(List<Child> children) {
        return children.stream()
                .map(ChildMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public static Child toEntity(ChildRequestDTO dto) {
        if (dto == null) return null;
        
        Child child = new Child();
        child.setName(dto.getName());
        child.setDob(dto.getDob());
        child.setGender(dto.getGender());
        
        return child;
    }
} 