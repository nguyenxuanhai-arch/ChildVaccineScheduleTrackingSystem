package edu.uth.childvaccinesystem.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
@Entity
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Vaccine name cannot be blank")
    @Size(max = 100, message = "Vaccine name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Manufacturer cannot be blank")
    @Size(max = 100, message = "Manufacturer name must not exceed 100 characters")
    private String manufacturer;

    @NotBlank(message = "Lot number cannot be blank")
    @Size(max = 50, message = "Lot number must not exceed 50 characters")
    private String lotNumber;

    @NotBlank(message = "Expiration date cannot be blank")
    private String expirationDate;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Column(name = "used")
    private Boolean used;

    public Vaccine() {}

    public Vaccine(String name, String manufacturer, String lotNumber, String expirationDate) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.lotNumber = lotNumber;
        this.expirationDate = expirationDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}