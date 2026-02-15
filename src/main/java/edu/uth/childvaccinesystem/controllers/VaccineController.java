package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.dtos.request.VaccineRequest;
import edu.uth.childvaccinesystem.dtos.response.VaccineResponse;
import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.mappers.VaccineMapper;
import edu.uth.childvaccinesystem.services.impl.VaccineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@RestController
@RequestMapping("/vaccine")
public class VaccineController {

    private final VaccineService vaccineService;
    private final VaccineMapper vaccineMapper;

    @PostMapping
    public VaccineResponse createVaccine(@RequestBody VaccineRequest request) {
        Vaccine savedVaccine = vaccineService.createVaccine(vaccineMapper.toEntity(request));
        return vaccineMapper.toResponse(savedVaccine);
    }
        
    @GetMapping("/vaccine-list")
    public Map<String, Object> getAllVaccines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Pageable pageable = PageRequest.of(page, size, 
            Sort.by(direction.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        
        Page<Vaccine> pageVaccines = vaccineService.getAllVaccinesWithPagination(pageable);
        
        Page<VaccineResponse> vaccines = pageVaccines.map(vaccineMapper::toResponse);

        Map<String, Object> response = new HashMap<>();
        response.put("vaccines", vaccines);
        response.put("currentPage", pageVaccines.getNumber());
        response.put("totalItems", pageVaccines.getTotalElements());
        response.put("totalPages", pageVaccines.getTotalPages());

        return response;
    }

    @PutMapping("/{id}")
    public Vaccine updateVaccine(@PathVariable Long id, @ModelAttribute Vaccine vaccine) {
        return vaccineService.updateVaccine(id, vaccine);
    }

    @DeleteMapping("/{id}")
    public long deleteVaccine(@PathVariable Long id) {
        return vaccineService.deleteVaccine(id);
    }
}
