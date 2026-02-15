package edu.uth.childvaccinesystem.controllers;

import edu.uth.childvaccinesystem.dtos.request.ChildRequest;
import edu.uth.childvaccinesystem.dtos.response.ChildResponse;
import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.services.impl.ChildService;
import edu.uth.childvaccinesystem.services.impl.UserService;
import edu.uth.childvaccinesystem.utils.ChildMapper;
import edu.uth.childvaccinesystem.utils.JwtUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

@RequiredArgsConstructor
@RestController
@RequestMapping("/child")
public class ChildController {
    private final ChildService childService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/simple-list")
    public ResponseEntity<List<ChildResponse>> getSimpleChildList(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String username = jwtUtil.extractUsername(jwt);
            System.out.println("Getting simplified children for parent: " + username);
            
            List<Child> children = childService.getChildrenByParentUsername(username);
            List<ChildResponse> childDTOs = ChildMapper.toDTOList(children);
            
            return ResponseEntity.ok(childDTOs);
        } catch (Exception e) {
            System.out.println("Error getting simplified children: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/by-parent")
    public ResponseEntity<List<ChildResponse>> getChildrenByToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String username = jwtUtil.extractUsername(jwt);
            System.out.println("Getting children for parent: " + username);

            List<Child> children = childService.getChildrenByParentUsername(username);
            System.out.println("Found " + children.size() + " children");

            List<ChildResponse> childDTOs = ChildMapper.toDTOList(children);

            if (childDTOs.isEmpty()) {
                System.out.println("No children found for parent: " + username);
                return ResponseEntity.ok(childDTOs);
            }

            for (ChildResponse dto : childDTOs) {
                if (dto != null) {
                    System.out.println("Child: " + dto.getName() + 
                                    ", DOB: " + dto.getDob() + 
                                    ", Gender: " + dto.getGender());
                }
            }

            return ResponseEntity.ok(childDTOs);
        } catch (Exception e) {
            System.out.println("Error getting children: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addChild(@RequestBody ChildRequest childRequest,
                                                     @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (childRequest.getName() == null || childRequest.getName().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "❌ Tên trẻ không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (childRequest.getDob() == null) {
                response.put("success", false);
                response.put("message", "❌ Ngày sinh không được để trống");
                return ResponseEntity.badRequest().body(response);
            }

            // Log input data for debugging
            System.out.println("Received child data:");
            System.out.println("Name: " + childRequest.getName());
            System.out.println("DOB: " + childRequest.getDob());
            System.out.println("Gender: " + childRequest.getGender());

            String jwt = token.substring(7);
            String username = jwtUtil.extractUsername(jwt);
            
            User parent;
            try {
                parent = userService.getUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                response.put("success", false);
                response.put("message", "❌ Không tìm thấy thông tin phụ huynh");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            Child child = ChildMapper.toEntity(childRequest);
            child.setParent(parent);
            child.setParentUsername(username);
            
            Child savedChild = childService.saveChild(child);
            
            if (savedChild != null && savedChild.getId() != null) {
                ChildResponse savedDTO = ChildMapper.toDTO(savedChild);
                
                // Log saved data for debugging
                if (savedDTO != null) {
                System.out.println("Saved child data:");
                    System.out.println("ID: " + savedDTO.getId());
                    System.out.println("Name: " + savedDTO.getName());
                    System.out.println("DOB: " + savedDTO.getDob());
                    System.out.println("Gender: " + savedDTO.getGender());
                }
                
                response.put("success", true);
                response.put("message", "✅ Thêm bé thành công!");
                response.put("child", savedDTO);
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

    // Modified to use DTO for form submission
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

            // Extract username from token
            String jwt = token.substring(7);
            String username = jwtUtil.extractUsername(jwt);
            
            // Get user from username
            User parent;
            try {
                parent = userService.getUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                response.put("success", false);
                response.put("message", "❌ Không tìm thấy thông tin phụ huynh");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Create new Child object (you could also use ChildRequest here)
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
            child.setParent(parent);
            child.setParentUsername(username);

            // Save child to database
            Child savedChild = childService.saveChild(child);
            
            if (savedChild != null && savedChild.getId() != null) {
                // Convert to DTO for response
                ChildResponse savedDTO = ChildMapper.toDTO(savedChild);
                
                response.put("success", true);
                response.put("message", "✅ Thêm bé thành công!");
                response.put("child", savedDTO);
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

    // Modified to use DTO for update
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateChild(
            @PathVariable Long id, 
            @RequestBody ChildRequest childRequest,
            @RequestHeader("Authorization") String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract username from token
            String jwt = token.substring(7);
            String username = jwtUtil.extractUsername(jwt);
            
            // Get the existing child
            Optional<Child> existingChildOpt = childService.getChildById(id);
            if (!existingChildOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Child not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            Child existingChild = existingChildOpt.get();
            
            // Check permissions (only parent can update their own child)
            if (existingChild.getParent() == null || !username.equals(existingChild.getParentUsername())) {
                response.put("success", false);
                response.put("message", "You don't have permission to update this child");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Update child from DTO
            existingChild.setName(childRequest.getName());
            existingChild.setDob(childRequest.getDob());
            existingChild.setGender(childRequest.getGender());
            
            // Use the existing service method
            String updatedId = childService.updateChild(id, existingChild);
            
            // Get the updated child
            Optional<Child> updatedChildOpt = childService.getChildById(Long.parseLong(updatedId));
            if (!updatedChildOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Error retrieving updated child");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
            // Convert to DTO for response
            ChildResponse updatedDTO = ChildMapper.toDTO(updatedChildOpt.get());
            
            response.put("success", true);
            response.put("message", "Child updated successfully");
            response.put("child", updatedDTO);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update child: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Modified to use DTO for delete response
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteChild(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract username from token
            String jwt = token.substring(7);
            String username = jwtUtil.extractUsername(jwt);
            
            // Get the child
            Optional<Child> existingChildOpt = childService.getChildById(id);
            if (!existingChildOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Child not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            Child existingChild = existingChildOpt.get();
            
            // Check permissions (only parent can delete their own child)
            if (existingChild.getParent() == null || !username.equals(existingChild.getParentUsername())) {
                response.put("success", false);
                response.put("message", "You don't have permission to delete this child");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Delete the child
            long deletedId = childService.deleteChild(id);
            
            response.put("success", true);
            response.put("message", "Child deleted successfully");
            response.put("id", deletedId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete child: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // New endpoint to get a child by ID using DTO
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getChildById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract username from token
            String jwt = token.substring(7);
            String username = jwtUtil.extractUsername(jwt);
            
            // Get the child
            Optional<Child> childOpt = childService.getChildById(id);
            if (!childOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Child not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            Child child = childOpt.get();
            
            // Check permissions (only parent can view their own child)
            if (child.getParent() == null || !username.equals(child.getParentUsername())) {
                response.put("success", false);
                response.put("message", "You don't have permission to view this child");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Convert to DTO
            ChildResponse childDTO = ChildMapper.toDTO(child);
            
            response.put("success", true);
            response.put("child", childDTO);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to get child: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/basic-by-parent")
    public ResponseEntity<List<Map<String, Object>>> getBasicChildrenData(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String username = jwtUtil.extractUsername(jwt);
            System.out.println("Getting basic children data for parent: " + username);
            
            List<Child> children = childService.getChildrenByParentUsername(username);
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Child child : children) {
                Map<String, Object> childData = new HashMap<>();
                childData.put("id", child.getId());
                childData.put("name", child.getName());
                childData.put("dob", child.getDob());
                childData.put("gender", child.getGender());
                
                // Calculate age in months
                if (child.getDob() != null) {
                    LocalDate now = LocalDate.now();
                    long months = java.time.temporal.ChronoUnit.MONTHS.between(child.getDob(), now);
                    childData.put("ageInMonths", months);
                }
                
                result.add(childData);
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("Error getting basic children data: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
