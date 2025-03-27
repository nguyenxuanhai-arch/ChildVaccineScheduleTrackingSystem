package edu.uth.childvaccinesystem.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
@Entity
public class Payment {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;  // Tên khách hàng
    private double amount;        // Số tiền thanh toán
    private String method;        // Phương thức thanh toán (chuyển khoản, ví điện tử, tiền mặt)
    private String status;        // Trạng thái (PENDING, COMPLETED, FAILED)
    private LocalDateTime createdAt; // Thời gian tạo

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
