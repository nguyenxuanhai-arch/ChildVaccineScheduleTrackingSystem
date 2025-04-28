package edu.uth.childvaccinesystem.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotificationRequest {
    private Long userId;
    private String message;
    private String title;
    private String type;
    private Boolean sendEmail; // Thêm lựa chọn gửi email
}
