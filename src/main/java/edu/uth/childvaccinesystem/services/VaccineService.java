package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.repositories.VaccineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VaccineService {

    @Autowired
    private VaccineRepository vaccineRepository;

    // Create (Thêm vaccine)
    public long createVaccine(Vaccine vaccine) {
        if (vaccineRepository.existsByName(vaccine.getName())) {
            throw new RuntimeException("Vaccine already exists");
            
        }
        return vaccineRepository.save(vaccine).getId();
    }

    // Read (Lấy thông tin vaccine theo ID)
    public Vaccine getVaccineById(Long id) {
        return vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccine not found"));
    }

    // Read (Lấy tất cả vaccine)
    public List<Vaccine> getAllVaccines() {
        return vaccineRepository.findAll();
    }

    // Update (Cập nhật vaccine)
    public long updateVaccine(Long id, Vaccine vaccineDetails) {
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccine not found"));

        vaccine.setName(vaccineDetails.getName());
        vaccine.setManufacturer(vaccineDetails.getManufacturer());
        vaccine.setLotNumber(vaccineDetails.getLotNumber());
        vaccine.setExpirationDate(vaccineDetails.getExpirationDate());

        return vaccineRepository.save(vaccine).getId();
    }

    // Delete (Xóa vaccine)
    public long deleteVaccine(Long id) {
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccine not found"));
        vaccineRepository.delete(vaccine);
        return id;
    }
}
