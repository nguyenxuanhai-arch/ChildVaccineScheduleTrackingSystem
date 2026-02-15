package edu.uth.childvaccinesystem.dtos.request;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long id;
    private double amount;
    private String status;
    private LocalDateTime paymentDate;
}