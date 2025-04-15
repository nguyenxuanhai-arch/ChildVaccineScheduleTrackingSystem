package edu.uth.childvaccinesystem.entities;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @JoinColumn(name = "child_id", nullable = false)
    private Long childId;

    @JoinColumn(name = "vaccine_id", nullable = false)
    private Long vaccineId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}