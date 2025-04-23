package edu.uth.childvaccinesystem.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {
    private Long appointmentId;
    private Integer rating;
    private String message;
} 