package edu.uth.childvaccinesystem.dtos.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccineResponse {
    private Long id;
    private String name;
    private String manufacturer;
    private String lotNumber;
    private String expirationDate;
    private Double price;
    private String imageBase64;
}
