package edu.uth.childvaccinesystem.controllers.res;

import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.entities.VaccinePackage;
import edu.uth.childvaccinesystem.services.UserService;
import edu.uth.childvaccinesystem.services.VaccineService;
import edu.uth.childvaccinesystem.services.VaccinePackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private VaccineService vaccineService;

    @Autowired
    private VaccinePackageService vaccinePackageService;

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping()
    public String dashboard(Model model) {
        // Add statistics to the model
        model.addAttribute("totalVaccines", vaccineService.getAllVaccines().size());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("todaySchedules", 0); // Replace with actual data when available
        model.addAttribute("pendingSchedules", 0); // Replace with actual data when available
        
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
        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // Compress image if needed
                byte[] compressedImage = compressImage(imageFile.getBytes());
                String base64Image = Base64.getEncoder().encodeToString(compressedImage);
                vaccine.setImageBase64(base64Image);
            }

            if (vaccine.getId() == null) {
                vaccineService.createVaccine(vaccine);
                redirectAttributes.addFlashAttribute("message", "Vaccine created successfully");
            } else {
                vaccineService.updateVaccine(vaccine.getId(), vaccine);
                redirectAttributes.addFlashAttribute("message", "Vaccine updated successfully");
            }
            return "redirect:/admin/vaccines";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vaccine", vaccine);
            return "admin/vaccines/vaccine-form";
        }
    }

    @PostMapping("/vaccines/delete/{id}")
    public String deleteVaccine(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vaccineService.deleteVaccine(id);
            redirectAttributes.addFlashAttribute("message", "Vaccine deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete vaccine: " + e.getMessage());
        }
        return "redirect:/admin/vaccines";
    }

    @GetMapping("/packages")
    public String packages(Model model) {
        List<VaccinePackage> packageList = vaccinePackageService.getAllPackages();
        model.addAttribute("packages", packageList);
        return "admin/packages/packages";
    }

    @GetMapping("/packages/individual")
    public String individualPackages(Model model) {
        List<VaccinePackage> packageList = vaccinePackageService.getPackagesByType(VaccinePackage.PackageType.INDIVIDUAL);
        model.addAttribute("packages", packageList);
        model.addAttribute("packageType", "Individual");
        return "admin/packages/packages";
    }

    @GetMapping("/packages/standard")
    public String standardPackages(Model model) {
        List<VaccinePackage> packageList = vaccinePackageService.getPackagesByType(VaccinePackage.PackageType.PACKAGE);
        model.addAttribute("packages", packageList);
        model.addAttribute("packageType", "Standard");
        return "admin/packages/packages";
    }

    @GetMapping("/packages/custom")
    public String customPackages(Model model) {
        List<VaccinePackage> packageList = vaccinePackageService.getPackagesByType(VaccinePackage.PackageType.CUSTOM);
        model.addAttribute("packages", packageList);
        model.addAttribute("packageType", "Custom");
        return "admin/packages/packages";
    }

    @GetMapping("/packages/new")
    public String showCreatePackageForm(Model model) {
        model.addAttribute("package", new VaccinePackage());
        model.addAttribute("vaccines", vaccineService.getAllVaccines());
        model.addAttribute("packageTypes", VaccinePackage.PackageType.values());
        return "admin/packages/package-form";
    }

    @GetMapping("/packages/edit/{id}")
    public String showEditPackageForm(@PathVariable Long id, Model model) {
        VaccinePackage vaccinePackage = vaccinePackageService.getPackageById(id)
                .orElse(null);

        if (vaccinePackage == null) {
            model.addAttribute("error", "Package not found");
            return "admin/packages/packages"; 
        }

        model.addAttribute("package", vaccinePackage);
        model.addAttribute("vaccines", vaccineService.getAllVaccines());
        model.addAttribute("packageTypes", VaccinePackage.PackageType.values());
        return "admin/packages/package-form";
    }

    @PostMapping("/packages/save")
    public String savePackage(
        @ModelAttribute VaccinePackage vaccinePackage,
        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
        @RequestParam(value = "selectedVaccineIds", required = false) List<Long> vaccineIds,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                byte[] compressedImage = compressImage(imageFile.getBytes());
                String base64Image = Base64.getEncoder().encodeToString(compressedImage);
                vaccinePackage.setImageBase64(base64Image);
            }

            // Handle vaccines
            if (vaccineIds != null && !vaccineIds.isEmpty()) {
                Set<Vaccine> vaccines = new HashSet<>();
                for (Long vaccineId : vaccineIds) {
                    vaccineService.getVaccineById(vaccineId).ifPresent(vaccines::add);
                }
                vaccinePackage.setVaccines(vaccines);
            }

            if (vaccinePackage.getId() == null) {
                vaccinePackageService.createPackage(vaccinePackage);
                redirectAttributes.addFlashAttribute("message", "Package created successfully");
            } else {
                vaccinePackageService.updatePackage(vaccinePackage.getId(), vaccinePackage);
                redirectAttributes.addFlashAttribute("message", "Package updated successfully");
            }
            return "redirect:/admin/packages";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("package", vaccinePackage);
            model.addAttribute("vaccines", vaccineService.getAllVaccines());
            model.addAttribute("packageTypes", VaccinePackage.PackageType.values());
            return "admin/packages/package-form";
        }
    }

    @PostMapping("/packages/delete/{id}")
    public String deletePackage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vaccinePackageService.deletePackage(id);
            redirectAttributes.addFlashAttribute("message", "Package deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete package: " + e.getMessage());
        }
        return "redirect:/admin/packages";
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

    private byte[] compressImage(byte[] imageData) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));
        
        // Create a new buffered image with RGB color model
        BufferedImage compressedImage = new BufferedImage(
            originalImage.getWidth(), 
            originalImage.getHeight(), 
            BufferedImage.TYPE_INT_RGB
        );
        
        // Draw the original image onto the new image
        compressedImage.createGraphics().drawImage(originalImage, 0, 0, null);
        
        // Write to ByteArrayOutputStream with JPEG compression
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(compressedImage, "jpg", outputStream);
        
        return outputStream.toByteArray();
    }
}