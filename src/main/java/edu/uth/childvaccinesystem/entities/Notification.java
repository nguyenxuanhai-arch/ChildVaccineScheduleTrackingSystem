package edu.uth.childvaccinesystem.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;  // ID người nhận thông báo

    @Column(name = "title")
    private String title;  // Tiêu đề thông báo (có thể null)

    @Column(name = "message", nullable = false, length = 500)
    private String message;  // Nội dung thông báo

    @Column(name = "sent_at")
    private LocalDateTime sentAt = LocalDateTime.now();  // Thời gian gửi

    @Column(name = "status", nullable = false)
    private boolean status = false;  // false = chưa đọc, true = đã đọc
    
    @Column(name = "type", length = 20)
    private String type;  // Loại thông báo: VACCINE, PACKAGE, PAYMENT, SYSTEM
    
    // Constructors, getters, setters đã được tạo bằng Lombok
}
