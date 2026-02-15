package edu.uth.childvaccinesystem.dtos.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {
    private Long appointmentId;
    private Integer rating;
    private String message;
} 