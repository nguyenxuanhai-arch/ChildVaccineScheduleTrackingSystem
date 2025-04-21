package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.services.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.uth.childvaccinesystem.utils.JwtUtil; // Để sử dụng JwtUtil
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/child")
public class ChildController {

    @Autowired
    private ChildService childService;

    @Autowired
    private JwtUtil jwtUtil; // Sử dụng JwtUtil để lấy thông tin từ token

    @GetMapping("/by-parent")
    public ResponseEntity<List<Child>> getChildrenByToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Bỏ "Bearer "
            String username = jwtUtil.extractUsername(jwt);
            System.out.println("Getting children for parent: " + username);

            List<Child> children = childService.getChildrenByParentUsername(username);
            System.out.println("Found " + children.size() + " children");

            if (children.isEmpty()) {
                System.out.println("No children found for parent: " + username);
                return ResponseEntity.ok(children); // Trả về danh sách rỗng thay vì No Content
            }

            // Log thông tin chi tiết của từng bé
            for (Child child : children) {
                System.out.println("Child: " + child.getName() + 
                                 ", DOB: " + child.getDob() + 
                                 ", Gender: " + child.getGender());
            }

            return ResponseEntity.ok(children);
        } catch (Exception e) {
            System.out.println("Error getting children: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addChild(@RequestBody Child child, @RequestHeader("Authorization") String token) {
        try {
            // Validate required fields
            if (child.getName() == null || child.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("❌ Tên trẻ không được để trống");
            }
            
            if (child.getDob() == null) {
                return ResponseEntity.badRequest().body("❌ Ngày sinh không được để trống");
            }

            // Log input data for debugging
            System.out.println("Received child data:");
            System.out.println("Name: " + child.getName());
            System.out.println("DOB: " + child.getDob());
            System.out.println("Gender: " + child.getGender());

            String parentUsername = jwtUtil.extractUsername(token.substring(7));
            child.setParentUsername(parentUsername);
            
            // Save child to database
            Child savedChild = childService.saveChild(child);
            
            if (savedChild != null && savedChild.getId() != null) {
                // Log saved data for debugging
                System.out.println("Saved child data:");
                System.out.println("ID: " + savedChild.getId());
                System.out.println("Name: " + savedChild.getName());
                System.out.println("DOB: " + savedChild.getDob());
                System.out.println("Gender: " + savedChild.getGender());
                return ResponseEntity.ok("✅ Thêm bé thành công!");
            } else {
                return ResponseEntity.status(500).body("❌ Thêm bé thất bại: Không thể lưu vào cơ sở dữ liệu");
            }
        } catch (Exception e) {
            System.out.println("Error saving child: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Thêm bé thất bại: " + e.getMessage());
        }
    }

    // Thêm endpoint mới để xử lý form thêm hồ sơ trẻ
    @PostMapping("/add-from-form")
    public ResponseEntity<Map<String, Object>> addChildFromForm(
            @RequestParam("name") String name,
            @RequestParam("dob") String dob,
            @RequestParam("gender") String gender,
            @RequestHeader("Authorization") String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate inputs
            if (name == null || name.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "❌ Tên trẻ không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (dob == null || dob.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "❌ Ngày sinh không được để trống");
                return ResponseEntity.badRequest().body(response);
            }

            // Create new Child object
            Child child = new Child();
            child.setName(name.trim());
            
            // Convert String to LocalDate
            try {
                LocalDate dobDate = LocalDate.parse(dob);
                child.setDob(dobDate);
            } catch (DateTimeParseException e) {
                response.put("success", false);
                response.put("message", "❌ Định dạng ngày sinh không hợp lệ");
                return ResponseEntity.badRequest().body(response);
            }
            
            child.setGender(gender.equals("male") ? "MALE" : "FEMALE");

            // Set parent username
            String parentUsername = jwtUtil.extractUsername(token.substring(7));
            child.setParentUsername(parentUsername);

            // Save child to database
            Child savedChild = childService.saveChild(child);
            
            if (savedChild != null && savedChild.getId() != null) {
                response.put("success", true);
                response.put("message", "✅ Thêm bé thành công!");
                response.put("child", savedChild);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "❌ Thêm bé thất bại: Không thể lưu vào cơ sở dữ liệu");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            System.out.println("Error saving child: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "❌ Thêm bé thất bại: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
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
