package edu.uth.childvaccinesystem.services.impl;

import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.mappers.VaccineMapper;
import edu.uth.childvaccinesystem.repositories.VaccineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VaccineService {

    private final VaccineRepository vaccineRepository;
    private final VaccineMapper vaccineMapper;

    public Vaccine createVaccine(Vaccine vaccine) {
        return vaccineRepository.save(vaccine);
    }

    public Optional<Vaccine> getVaccineById(Long id) {
        return vaccineRepository.findById(id);
    }

    public List<Vaccine> getAllVaccines() {
        return vaccineRepository.findAll();
    }

    public Page<Vaccine> getAllVaccinesWithPagination(Pageable pageable) {
        return vaccineRepository.findAll(pageable);
    }

    public Vaccine updateVaccine(Long id, Vaccine vaccineDetails) {
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vaccine "));
        vaccineMapper.updateVaccineFromRequest(vaccineDetails, vaccine);

        return vaccineRepository.save(vaccine);
    }

    public long deleteVaccine(Long id) {
        Vaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vaccine "));
        vaccineRepository.delete(vaccine);
        return id;
    }
}
