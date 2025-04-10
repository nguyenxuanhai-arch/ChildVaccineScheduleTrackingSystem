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
    private Double price; // Giá thành vaccine

    @Lob
    @Column(name = "image_base64", columnDefinition = "TEXT")
    private String imageBase64; // Hình ảnh vaccine dưới dạng Base64

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Vaccine() {}

    public Vaccine(String name, String manufacturer, String lotNumber, String expirationDate, Double price) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.lotNumber = lotNumber;
        this.expirationDate = expirationDate;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}