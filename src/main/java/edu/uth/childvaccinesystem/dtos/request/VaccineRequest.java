package edu.uth.childvaccinesystem.dtos.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class VaccineRequest {
    private String name;
    private String manufacturer;
    private String lotNumber;
    private String expirationDate;
    private Double price;
    private MultipartFile image; // Hình ảnh vaccine
}
