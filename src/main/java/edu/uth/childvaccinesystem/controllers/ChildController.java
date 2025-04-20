package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.services.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.uth.childvaccinesystem.utils.JwtUtil; // Để sử dụng JwtUtil
import java.util.List;

@RestController
@RequestMapping("/child")
public class ChildController {

    @Autowired
    private ChildService childService;

    @Autowired
    private JwtUtil jwtUtil; // Sử dụng JwtUtil để lấy thông tin từ token

    @GetMapping("/by-parent")
    public ResponseEntity<List<Child>> getChildrenByToken(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Bỏ "Bearer "
        String username = jwtUtil.extractUsername(jwt); // Giải mã token để lấy username
        System.out.println("Username: " + username);  // Debug log

        List<Child> children = childService.getChildrenByParentUsername(username);
        if (children.isEmpty()) {
            System.out.println("Không có trẻ em nào cho phụ huynh: " + username); // Debug log
            return ResponseEntity.noContent().build(); // Trả về No Content nếu không có trẻ em
        }

        return ResponseEntity.ok(children);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addChild(@RequestBody Child child, @RequestHeader("Authorization") String token) {
        try {
            String parentUsername = jwtUtil.extractUsername(token.substring(7)); // Giải mã token để lấy username
            child.setParentUsername(parentUsername);  // Gán parentUsername cho trẻ em
            
            // Lưu bé vào cơ sở dữ liệu
            childService.saveChild(child);
            return ResponseEntity.ok("✅ Thêm bé thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Thêm bé thất bại: " + e.getMessage());  // Trả về thông báo lỗi nếu có vấn đề
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateChild(@PathVariable Long id, @RequestBody Child childDetails) {
        try {
            long updatedId = Long.parseLong(childService.updateChild(id, childDetails));
            return ResponseEntity.ok(updatedId);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteChild(@PathVariable Long id) {
        try {
            long deletedId = childService.deleteChild(id);
            return ResponseEntity.ok(deletedId);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
