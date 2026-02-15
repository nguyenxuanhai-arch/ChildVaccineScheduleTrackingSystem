package edu.uth.childvaccinesystem.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@Builder(toBuilder = true)
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    private double amount;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private LocalDateTime paymentDate;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
