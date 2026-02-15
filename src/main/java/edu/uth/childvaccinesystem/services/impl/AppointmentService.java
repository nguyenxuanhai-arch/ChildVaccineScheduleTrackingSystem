package edu.uth.childvaccinesystem.services.impl;

import edu.uth.childvaccinesystem.services.interfaces.AppointmentServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.entities.Appointment.AppointmentStatus;
import edu.uth.childvaccinesystem.entities.Appointment.AppointmentType;
import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.entities.VaccinePackage;
import edu.uth.childvaccinesystem.dtos.request.AppointmentRequest;
import edu.uth.childvaccinesystem.dtos.request.PackageAppointmentRequest;
import edu.uth.childvaccinesystem.repositories.AppointmentRepository;
import edu.uth.childvaccinesystem.repositories.ChildRepository;
import edu.uth.childvaccinesystem.repositories.VaccineRepository;
import edu.uth.childvaccinesystem.repositories.VaccinePackageRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AppointmentService implements AppointmentServiceInterface {
    private final AppointmentRepository appointmentRepository;
    private final ChildRepository childRepository;
    private final VaccineRepository vaccineRepository;
    private final VaccinePackageRepository vaccinePackageRepository;

    @Override
    public Appointment bookAppointment(AppointmentRequest request) {
        Child child = childRepository.findById(request.getChildId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trẻ em"));

        Vaccine vaccine = vaccineRepository.findById(request.getVaccineId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vắc xin"));

        Appointment appointment = Appointment.builder()
                .child(child)
                .vaccine(vaccine)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .notes(request.getNotes())
                .status(AppointmentStatus.SCHEDULED)
                .type(AppointmentType.VACCINE)
                .createAt(LocalDate.now())
                .build();

        return appointmentRepository.save(appointment);
    }

    public Appointment bookPackageAppointment(PackageAppointmentRequest request) {
        Child child = childRepository.findById(request.getChildId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trẻ em"));

        VaccinePackage vaccinePackage = vaccinePackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói vắc xin"));
                
        int childAgeInMonths = calculateAgeInMonths(child.getDob());
        if (childAgeInMonths < vaccinePackage.getAgeRangeStart() || childAgeInMonths > vaccinePackage.getAgeRangeEnd()) {
            throw new RuntimeException("Gói vắc xin không phù hợp với độ tuổi của trẻ");
        }

        Appointment appointment = new Appointment();
        appointment.setChild(child);
        appointment.setVaccinePackage(vaccinePackage);

        Set<Vaccine> packageVaccines = vaccinePackage.getVaccines();
        if (packageVaccines != null && !packageVaccines.isEmpty()) {
            appointment.setVaccine(packageVaccines.iterator().next());
        } else {
            // If no vaccines in the package (shouldn't happen in normal scenarios)
            throw new RuntimeException("Gói vắc xin không chứa vaccine nào");
        }

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setType(AppointmentType.PACKAGE);
        appointment.setCreateAt(LocalDate.now());

        return appointmentRepository.save(appointment);
    }
    
    private int calculateAgeInMonths(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        return (currentDate.getYear() - birthDate.getYear()) * 12 + (currentDate.getMonthValue() - birthDate.getMonthValue());
    }

    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc hẹn"));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByUsername(String username) {
        return appointmentRepository.findByChildParentUsername(username);
    }

    public List<Appointment> getAppointmentsByChildId(Long childId) {
        try {
            List<Appointment> appointments = appointmentRepository.findByChildIdWithDetails(childId);
            return appointments;
        } catch (Exception e) {
            System.err.println("Error fetching appointments for child ID " + childId + ": " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc hẹn"));
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public void syncChildAppointments(Child child) {
        List<Appointment> childAppointments = appointmentRepository.findByChildId(child.getId());
        
        if (child.getAppointments() == null) {
            child.setAppointments(new HashSet<>());
        }
        
        child.getAppointments().clear();
        child.getAppointments().addAll(childAppointments);
    }

    public void saveAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }
}
