package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.services.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vaccine")
public class VaccineController {

    @Autowired
    private VaccineService vaccineService;

    // Create
    @PostMapping("/vaccine")
    public long createVaccine(@RequestBody Vaccine vaccine) {
        return vaccineService.createVaccine(vaccine);
    }

    // Read (Lấy tất cả vaccine)
    @GetMapping("vaccines")
    public List<Vaccine> getAllVaccines() {
        return vaccineService.getAllVaccines();
    }

    // Update
    @PutMapping("vaccine/{id}")
    public long updateVaccine(@PathVariable Long id, @RequestBody Vaccine vaccine) {
        return vaccineService.updateVaccine(id, vaccine);
    }

    // Delete
    @DeleteMapping("vaccine/{id}")
    public long deleteVaccine(@PathVariable Long id) {
        return vaccineService.deleteVaccine(id);
    }
}
