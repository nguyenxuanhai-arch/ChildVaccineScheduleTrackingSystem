package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.dtos.response.VaccineResponse;
    import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.services.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

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
        
    // Read (Lấy tất cả vaccine)
    @GetMapping("/vaccine-list")
    public List<VaccineResponse> getAllVaccines() {
        List<Vaccine> vaccines = vaccineService.getAllVaccines();
        return vaccines.stream().map(vaccine -> {
            VaccineResponse response = new VaccineResponse();
            response.setId(vaccine.getId());
            response.setName(vaccine.getName());
            response.setManufacturer(vaccine.getManufacturer());
            response.setLotNumber(vaccine.getLotNumber());
            response.setExpirationDate(vaccine.getExpirationDate());
            response.setPrice(vaccine.getPrice());
            response.setImageBase64(vaccine.getImageBase64()); // Trả về Base64
            return response;
        }).toList();
    }

    // Update
    @PutMapping("/{id}")
    public long updateVaccine(@PathVariable Long id, @ModelAttribute Vaccine vaccine) {
        return vaccineService.updateVaccine(id, vaccine);
    }

    // Delete
    @DeleteMapping("/{id}")
    public long deleteVaccine(@PathVariable Long id) {
        return vaccineService.deleteVaccine(id);
    }
}
