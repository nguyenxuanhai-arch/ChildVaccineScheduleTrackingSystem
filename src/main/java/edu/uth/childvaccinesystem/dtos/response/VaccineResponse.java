package edu.uth.childvaccinesystem.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VaccineResponse {
    private Long id;
    private String name;
    private String manufacturer;
    private String lotNumber;
    private String expirationDate;
    private Double price;
    private String imageBase64;
}
