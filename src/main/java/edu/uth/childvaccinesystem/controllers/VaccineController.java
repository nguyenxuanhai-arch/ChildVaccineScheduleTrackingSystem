package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.dtos.response.VaccineResponse;
import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.services.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/vaccine")
public class VaccineController {

    @Autowired
    private VaccineService vaccineService;

    // Create
    @PostMapping
    public VaccineResponse createVaccine(
        @RequestParam("name") String name,
        @RequestParam("manufacturer") String manufacturer,
        @RequestParam("lotNumber") String lotNumber,
        @RequestParam("expirationDate") String expirationDate,
        @RequestParam("price") Double price,
        @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        Vaccine vaccine = new Vaccine();
        vaccine.setName(name);
        vaccine.setManufacturer(manufacturer);
        vaccine.setLotNumber(lotNumber);
        vaccine.setExpirationDate(expirationDate);
        vaccine.setPrice(price);

        if (image != null && !image.isEmpty()) {
            try {
                String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
                vaccine.setImageBase64(base64Image);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi xử lý hình ảnh", e);
            }
        }

        Vaccine savedVaccine = vaccineService.createVaccine(vaccine);

        VaccineResponse response = new VaccineResponse();
        response.setId(savedVaccine.getId());
        response.setName(savedVaccine.getName());
        response.setManufacturer(savedVaccine.getManufacturer());
        response.setLotNumber(savedVaccine.getLotNumber());
        response.setExpirationDate(savedVaccine.getExpirationDate());
        response.setPrice(savedVaccine.getPrice());
        response.setImageBase64(savedVaccine.getImageBase64()); // Trả về Base64

        return response;
    }
        
    // Read (Lấy tất cả vaccine với phân trang)
    @GetMapping("/vaccine-list")
    public Map<String, Object> getAllVaccines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Pageable pageable = PageRequest.of(page, size, 
            Sort.by(direction.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        
        org.springframework.data.domain.Page<Vaccine> pageVaccines = vaccineService.getAllVaccinesWithPagination(pageable);
        
        List<VaccineResponse> vaccines = pageVaccines.getContent().stream().map(vaccine -> {
            VaccineResponse response = new VaccineResponse();
            response.setId(vaccine.getId());
            response.setName(vaccine.getName());
            response.setManufacturer(vaccine.getManufacturer());
            response.setLotNumber(vaccine.getLotNumber());
            response.setExpirationDate(vaccine.getExpirationDate());
            response.setPrice(vaccine.getPrice());
            response.setImageBase64(vaccine.getImageBase64());
            return response;
        }).collect(toList());

        Map<String, Object> response = new HashMap<>();
        response.put("vaccines", vaccines);
        response.put("currentPage", pageVaccines.getNumber());
        response.put("totalItems", pageVaccines.getTotalElements());
        response.put("totalPages", pageVaccines.getTotalPages());

        return response;
    }

    // Update
    @PutMapping("/{id}")
    public Vaccine updateVaccine(@PathVariable Long id, @ModelAttribute Vaccine vaccine) {
        return vaccineService.updateVaccine(id, vaccine);
    }

    // Delete
    @DeleteMapping("/{id}")
    public long deleteVaccine(@PathVariable Long id) {
        return vaccineService.deleteVaccine(id);
    }
}
