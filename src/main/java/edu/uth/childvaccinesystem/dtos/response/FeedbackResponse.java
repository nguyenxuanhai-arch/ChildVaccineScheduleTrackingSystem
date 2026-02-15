package edu.uth.childvaccinesystem.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {
    private Long id;
    private String username;
    private String name;
    private Long appointmentId;
    private String message;
    private int rating;
    private LocalDateTime createdAt;

}