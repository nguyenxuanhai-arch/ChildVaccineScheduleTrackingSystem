package edu.uth.childvaccinesystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "vaccine_package")
public class VaccinePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private PackageType type;
    
    private Double price;
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imageBase64;
    
    private int ageRangeStart; // Age in months
    
    private int ageRangeEnd; // Age in months
    
    private boolean featured;
    
    @ManyToMany
    @JoinTable(
        name = "package_vaccine",
        joinColumns = @JoinColumn(name = "package_id"),
        inverseJoinColumns = @JoinColumn(name = "vaccine_id")
    )
    private Set<Vaccine> vaccines = new HashSet<>();
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    public enum PackageType {
        INDIVIDUAL("Tiêm lẻ"),
        PACKAGE("Trọn gói"),
        CUSTOM("Cá thể hóa");
        
        private final String displayName;
        
        PackageType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 