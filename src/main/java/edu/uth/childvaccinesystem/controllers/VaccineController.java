package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.services.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vaccine")
public class VaccineController {

    @Autowired
    private VaccineService vaccineService;

    // Create
    @PostMapping()
    public long createVaccine(@RequestBody Vaccine vaccine) {
        return vaccineService.createVaccine(vaccine);
    }

    // Read (Lấy tất cả vaccine)
    @GetMapping()
    public List<Vaccine> getAllVaccines() {
        return vaccineService.getAllVaccines();
    }

    // Update
    @PutMapping("/{id}")
    public long updateVaccine(@PathVariable Long id, @RequestBody Vaccine vaccine) {
        return vaccineService.updateVaccine(id, vaccine);
    }

    // Delete
    @DeleteMapping("/{id}")
    public long deleteVaccine(@PathVariable Long id) {
        return vaccineService.deleteVaccine(id);
    }
}
