package edu.uth.childvaccinesystem.dtos.request;

import java.time.LocalDateTime;

import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class PaymentRequest {
    private Long id;
    private double amount;
    private String status;
    private LocalDateTime paymentDate;
}