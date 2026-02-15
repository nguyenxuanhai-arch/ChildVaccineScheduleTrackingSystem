package edu.uth.childvaccinesystem.dtos.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccineRequest {
    private String name;
    private String manufacturer;
    private String lotNumber;
    private String expirationDate;
    private Double price;
    private MultipartFile image;
}
