package edu.uth.childvaccinesystem.controllers.res;

import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.services.UserService;
import edu.uth.childvaccinesystem.services.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private VaccineService vaccineService;

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping()
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        List<User> userList = userService.getAllUsers();
        model.addAttribute("users", userList);
        return "admin/users/users";
    }

    @GetMapping("/users/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "admin/users/user-details";
    }

    @GetMapping("/users/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User()); // Truyền một đối tượng User rỗng vào model
        return "admin/users/user-form"; // Trả về view form tạo người dùng
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user); // Truyền đối tượng User hiện tại vào model
        return "admin/users/user-form"; // Trả về view form sửa người dùng
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user, Model model) {
        try {
            userService.createUser(user); // Lưu hoặc cập nhật người dùng
            return "redirect:/admin/users"; // Chuyển hướng về danh sách người dùng
        } catch (RuntimeException e) {
            model.addAttribute("user", user);
            model.addAttribute("error", e.getMessage()); // Truyền thông báo lỗi vào model
            return "admin/users/user-form"; // Quay lại form với thông báo lỗi
        }
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute User user) {
        userService.createUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User userDetails) {
        userService.updateUser(id, userDetails);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/vaccines")
    public String vaccines(Model model) {
        List<Vaccine> vaccineList = vaccineService.getAllVaccines();
        model.addAttribute("vaccines", vaccineList);
        return "admin/vaccines/vaccines";
    }

    @GetMapping("/vaccines/new")
    public String showCreateVaccineForm(Model model) {
        model.addAttribute("vaccine", new Vaccine());
        return "admin/vaccines/vaccine-form";
    }

    @GetMapping("/vaccines/edit/{id}")
    public String showEditVaccineForm(@PathVariable Long id, Model model) {
        Vaccine vaccine = vaccineService.getVaccineById(id)
                .orElse(null);

        if (vaccine == null) {
            model.addAttribute("error", "Vaccine not found");
            return "admin/vaccines/vaccines"; // Quay lại danh sách vaccine với thông báo lỗi
        }

        model.addAttribute("vaccine", vaccine);
        return "admin/vaccines/vaccine-form";
    }

    @PostMapping("/vaccines/save")
    public String saveVaccine(
        @ModelAttribute Vaccine vaccine,
        @RequestParam(value = "imageBase64", required = false) MultipartFile imageFile,
        Model model
    ) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // Chuyển đổi hình ảnh sang chuỗi Base64
                String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
                vaccine.setImageBase64(base64Image);
            }

            if (vaccine.getId() == null) {
                vaccineService.createVaccine(vaccine); // Tạo mới vaccine
            } else {
                vaccineService.updateVaccine(vaccine.getId(), vaccine); // Cập nhật vaccine
            }
            return "redirect:/admin/vaccines"; // Chuyển hướng về danh sách vaccine
        } catch (Exception e) {
            model.addAttribute("vaccine", vaccine); // Truyền lại dữ liệu vaccine vào model
            model.addAttribute("error", "Failed to save vaccine: " + e.getMessage()); // Truyền thông báo lỗi vào model
            return "admin/vaccines/vaccine-form"; // Quay lại form với thông báo lỗi
        }
    }

    @PostMapping("/vaccines/delete/{id}")
    public String deleteVaccine(@PathVariable Long id) {
        vaccineService.deleteVaccine(id);
        return "redirect:/admin/vaccines";
    }

    @GetMapping("/reports")
    public String reports() {
        return "admin/reports/reports";
    }

    @GetMapping("/payments")
    public String payments() {
        return "admin/payments/payments";
    }

    @GetMapping("/notifications")
    public String notifications() {
        return "admin/notifications/notifications";
    }

    @GetMapping("/feedbacks")
    public String feedbacks() {
        return "admin/feedbacks/feedbacks";
    }

    @GetMapping("/children")
    public String children() {
        return "admin/children/children";
    }
    
    @GetMapping("/appointments")
    public String appointments() {
        return "admin/appointments/appointments";
    }
}