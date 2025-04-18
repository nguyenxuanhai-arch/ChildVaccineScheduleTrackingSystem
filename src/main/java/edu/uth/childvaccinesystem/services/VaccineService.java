package edu.uth.childvaccinesystem.services;

import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.repositories.VaccineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VaccineService {

    @Autowired
    private VaccineRepository vaccineRepository;

    // Create (Thêm vaccine)
    public Vaccine createVaccine(Vaccine vaccine) {
        if (vaccineRepository.existsByName(vaccine.getName())) {
            throw new RuntimeException("Vaccine name already exists");
        }
        return vaccineRepository.save(vaccine);
    }

    // Read (Lấy thông tin vaccine theo ID)
    public Optional<Vaccine> getVaccineById(Long id) {
        return vaccineRepository.findById(id);
    }

    // Read (Lấy tất cả vaccine)
    public List<Vaccine> getAllVaccines() {
        return vaccineRepository.findAll();
    }

    // Read (Lấy tất cả vaccine với phân trang)
    public Page<Vaccine> getAllVaccinesWithPagination(Pageable pageable) {
        return vaccineRepository.findAll(pageable);
    }

    // Update (Cập nhật vaccine)
    public Vaccine updateVaccine(Long id, Vaccine vaccineDetails) {
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccine not found"));

        // Kiểm tra trùng lặp tên vaccine (trừ chính vaccine hiện tại)
        if (!vaccine.getName().equals(vaccineDetails.getName()) &&
                vaccineRepository.existsByName(vaccineDetails.getName())) {
            throw new RuntimeException("Vaccine name already exists");
        }

        vaccine.setName(vaccineDetails.getName());
        vaccine.setManufacturer(vaccineDetails.getManufacturer());
        vaccine.setLotNumber(vaccineDetails.getLotNumber());
        vaccine.setExpirationDate(vaccineDetails.getExpirationDate());
        vaccine.setPrice(vaccineDetails.getPrice());
        vaccine.setImageBase64(vaccineDetails.getImageBase64());

        return vaccineRepository.save(vaccine);
    }

    // Delete (Xóa vaccine)
    public long deleteVaccine(Long id) {
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaccine not found"));
        vaccineRepository.delete(vaccine);
        return id;
    }
}
