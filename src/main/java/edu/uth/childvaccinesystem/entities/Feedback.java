package edu.uth.childvaccinesystem.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feedbacks")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_username", nullable = false)
    private User user;
    
    @OneToOne
    @JoinColumn(name = "appointment_id", unique = true)
    private Appointment appointment;

    private String message;
    private int rating;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
