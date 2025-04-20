package edu.uth.childvaccinesystem.services;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private VaccineRepository vaccineRepository;
    
    @Autowired
    private VaccinePackageRepository vaccinePackageRepository;

    public Appointment bookAppointment(AppointmentRequest request) {
        Child child = childRepository.findById(request.getChildId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trẻ em"));

        Vaccine vaccine = vaccineRepository.findById(request.getVaccineId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vắc xin"));

        Appointment appointment = new Appointment();
        appointment.setChild(child);
        appointment.setVaccine(vaccine);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setType(AppointmentType.VACCINE);
        appointment.setCreateAt(LocalDate.now());

        return appointmentRepository.save(appointment);
    }
    
    public Appointment bookPackageAppointment(PackageAppointmentRequest request) {
        Child child = childRepository.findById(request.getChildId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trẻ em"));

        VaccinePackage vaccinePackage = vaccinePackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy gói vắc xin"));
                
        // Check if package is suitable for child's age
        int childAgeInMonths = calculateAgeInMonths(child.getDob());
        if (childAgeInMonths < vaccinePackage.getAgeRangeStart() || childAgeInMonths > vaccinePackage.getAgeRangeEnd()) {
            throw new RuntimeException("Gói vắc xin không phù hợp với độ tuổi của trẻ");
        }

        Appointment appointment = new Appointment();
        appointment.setChild(child);
        appointment.setVaccinePackage(vaccinePackage);
        
        // Fix for "vaccine_id cannot be null" error
        // Get a representative vaccine from the package to set as the default
        Set<Vaccine> packageVaccines = vaccinePackage.getVaccines();
        if (packageVaccines != null && !packageVaccines.isEmpty()) {
            // Set the first vaccine from the package as the default
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
            System.out.println("Fetching appointments for child ID: " + childId);
            // Use the enhanced query with joins instead of the basic finder method
            List<Appointment> appointments = appointmentRepository.findByChildIdWithDetails(childId);
            System.out.println("Successfully fetched " + appointments.size() + " appointments");
            return appointments;
        } catch (Exception e) {
            System.err.println("Error fetching appointments for child ID " + childId + ": " + e.getMessage());
            e.printStackTrace();
            // Return empty list instead of throwing exception
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
    
    /**
     * Ensures that the appointments are properly linked to the child entity
     * This helps maintain the bi-directional relationship between Child and Appointment
     */
    public void syncChildAppointments(Child child) {
        // Get all appointments for this child from the database
        List<Appointment> childAppointments = appointmentRepository.findByChildId(child.getId());
        
        // Initialize the appointments collection if needed
        if (child.getAppointments() == null) {
            child.setAppointments(new HashSet<>());
        }
        
        // Add all appointments to the child entity
        child.getAppointments().clear();
        child.getAppointments().addAll(childAppointments);
    }
}
