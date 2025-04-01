package edu.uth.childvaccinesystem.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

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

    public Vaccine() {}

    public Vaccine(String name, String manufacturer, String lotNumber, String expirationDate) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.lotNumber = lotNumber;
        this.expirationDate = expirationDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Vaccine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", lotNumber='" + lotNumber + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vaccine vaccine = (Vaccine) o;
        return Objects.equals(id, vaccine.id) &&
                Objects.equals(name, vaccine.name) &&
                Objects.equals(manufacturer, vaccine.manufacturer) &&
                Objects.equals(lotNumber, vaccine.lotNumber) &&
                Objects.equals(expirationDate, vaccine.expirationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, manufacturer, lotNumber, expirationDate);
    }
}