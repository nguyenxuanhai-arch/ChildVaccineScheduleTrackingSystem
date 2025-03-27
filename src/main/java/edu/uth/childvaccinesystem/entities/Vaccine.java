package edu.uth.childvaccinesystem.entities;

import jakarta.persistence.*;

@Entity
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String manufacturer;

    private String lotNumber;

    private String expirationDate;

    public Vaccine() {}

    public Vaccine(String name, String manufacturer, String lotNumber, String expirationDate) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.lotNumber = lotNumber;
        this.expirationDate = expirationDate;
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
}