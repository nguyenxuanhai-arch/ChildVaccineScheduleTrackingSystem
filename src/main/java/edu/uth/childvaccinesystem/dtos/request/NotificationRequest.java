package edu.uth.childvaccinesystem.dtos.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private Long userId;
    private String message;
    private String title;
    private String type;
    private Boolean sendEmail;
}
