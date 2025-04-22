package edu.uth.childvaccinesystem.controllers.res;

import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.entities.Vaccine;
import edu.uth.childvaccinesystem.entities.VaccinePackage;
import edu.uth.childvaccinesystem.entities.Payment;
import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.services.UserService;
import edu.uth.childvaccinesystem.services.VaccineService;
import edu.uth.childvaccinesystem.services.VaccinePackageService;
import edu.uth.childvaccinesystem.services.PaymentService;
import edu.uth.childvaccinesystem.services.AppointmentService;
import edu.uth.childvaccinesystem.services.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private VaccineService vaccineService;

    @Autowired
    private VaccinePackageService vaccinePackageService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ChildService childService;

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
    public String showCreateUserForm(Model model, @RequestParam(required = false) String role) {
        User user = new User();
        
        // Nếu có role được chỉ định từ query string, thiết lập mặc định
        if (role != null && !role.isEmpty()) {
            if (!role.startsWith("")) {
                role = "" + role;
            }
            user.setRole(role);
            
            // Thêm thông báo gợi ý
            model.addAttribute("roleHint", "Tạo người dùng với vai trò: " + role);
        }
        
        model.addAttribute("user", user);
        return "admin/users/user-form";
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
            // Thêm thiết lập createdAt cho người dùng mới để tránh lỗi NOT NULL
            if (user.getId() == null) {
                user.setCreatedAt(LocalDateTime.now());
                System.out.println("Setting createdAt for new user: " + user.getUsername());
            }
            
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
    public String payments(Model model) {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            System.out.println("Loaded " + payments.size() + " payments from database");
            
            model.addAttribute("payments", payments);
        } catch (Exception e) {
            System.err.println("Error loading payments: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Không thể tải dữ liệu thanh toán: " + e.getMessage());
            model.addAttribute("payments", List.of()); // Trả về danh sách rỗng
        }
        return "admin/payments/payments";
    }

    @GetMapping("/payments/{id}")
    public String viewPaymentDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            
            if (payment != null) {
                model.addAttribute("payment", payment);
                return "admin/payments/payment-details";
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin thanh toán với ID: " + id);
                return "redirect:/admin/payments";
            }
        } catch (Exception e) {
            logger.error("Error loading payment details: ", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tải thông tin thanh toán: " + e.getMessage());
            return "redirect:/admin/payments";
        }
    }
    
    @PostMapping("/payments/update-status")
    public String updatePaymentStatus(
            @RequestParam("paymentId") Long paymentId,
            @RequestParam("status") String status,
            @RequestParam(value = "notes", required = false) String additionalNotes,
            RedirectAttributes redirectAttributes) {
        try {
            if (paymentId == null) {
                redirectAttributes.addFlashAttribute("error", "ID thanh toán không hợp lệ");
                return "redirect:/admin/payments";
            }
            
            if (status == null || status.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Trạng thái thanh toán không hợp lệ");
                return "redirect:/admin/payments";
            }
            
            // Lấy thông tin thanh toán
            Payment payment = paymentService.getPaymentById(paymentId);
            if (payment == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thanh toán với ID: " + paymentId);
                return "redirect:/admin/payments";
            }
            
            // Cập nhật thông tin
            payment.setStatus(status);
            
            // Thêm ghi chú
            String notes = "Cập nhật trạng thái từ [" + payment.getStatus() + "] sang [" + status + "] lúc: " + LocalDateTime.now();
            if (additionalNotes != null && !additionalNotes.trim().isEmpty()) {
                notes += " - Ghi chú: " + additionalNotes;
            }
            
            if (payment.getNotes() != null && !payment.getNotes().isEmpty()) {
                payment.setNotes(payment.getNotes() + " | " + notes);
            } else {
                payment.setNotes(notes);
            }
            
            // Lưu thông tin thanh toán
            paymentService.savePayment(payment);
            
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thanh toán thành công");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái thanh toán: " + e.getMessage());
        }
        
        return "redirect:/admin/payments";
    }

    @GetMapping("/payments/ajax/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPaymentAjax(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Payment payment = paymentService.getPaymentById(id);
            
            if (payment != null) {
                Map<String, Object> paymentData = new HashMap<>();
                paymentData.put("id", payment.getId());
                paymentData.put("amount", payment.getAmount());
                paymentData.put("status", payment.getStatus());
                paymentData.put("paymentMethod", payment.getPaymentMethod());
                paymentData.put("notes", payment.getNotes());
                
                if (payment.getPaymentDate() != null) {
                    paymentData.put("paymentDate", payment.getPaymentDate().toString());
                }
                
                // Include appointment info if available
                if (payment.getAppointment() != null) {
                    Map<String, Object> appointmentData = new HashMap<>();
                    Appointment appointment = payment.getAppointment();
                    
                    appointmentData.put("id", appointment.getId());
                    appointmentData.put("status", appointment.getStatus());
                    
                    if (appointment.getAppointmentDate() != null) {
                        appointmentData.put("date", appointment.getAppointmentDate().toString());
                    }
                    
                    appointmentData.put("time", appointment.getAppointmentTime());
                    appointmentData.put("type", appointment.getType());
                    
                    // Add child info if available
                    if (appointment.getChild() != null) {
                        Map<String, Object> childData = new HashMap<>();
                        Child child = appointment.getChild();
                        
                        childData.put("id", child.getId());
                        childData.put("name", child.getName());
                        
                        appointmentData.put("child", childData);
                    }
                    
                    // Add vaccine or package info
                    if ("VACCINE".equals(appointment.getType()) && appointment.getVaccine() != null) {
                        appointmentData.put("vaccineName", appointment.getVaccine().getName());
                    } else if ("PACKAGE".equals(appointment.getType()) && appointment.getVaccinePackage() != null) {
                        appointmentData.put("packageName", appointment.getVaccinePackage().getName());
                    }
                    
                    paymentData.put("appointment", appointmentData);
                }
                
                response.put("success", true);
                response.put("payment", paymentData);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin thanh toán");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Error getting payment details via AJAX: ", e);
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra khi tải thông tin thanh toán: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/notifications")
    public String notifications() {
        return "admin/notifications/notifications";
    }

    @GetMapping("/feedbacks")
    public String feedbacks() {
        return "admin/feedbacks/feedbacks";
    }

    @GetMapping("/appointments")
    public String appointments(Model model) {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            model.addAttribute("appointments", appointments);
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải danh sách đặt lịch: " + e.getMessage());
            model.addAttribute("appointments", List.of()); // Trả về danh sách rỗng
        }
        return "admin/appointments/appointments";
    }
    
    @GetMapping("/appointments/view/{id}")
    public String viewAppointmentDetails(@PathVariable Long id, Model model) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            model.addAttribute("appointment", appointment);
            return "admin/appointments/appointment-details";
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải thông tin đặt lịch: " + e.getMessage());
            return "redirect:/admin/appointments";
        }
    }
    
    @GetMapping("/appointments/{id}")
    public String getAppointmentById(@PathVariable Long id, Model model) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            model.addAttribute("appointment", appointment);
            return "admin/appointments/appointment-details";
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải thông tin đặt lịch: " + e.getMessage());
            return "redirect:/admin/appointments";
        }
    }
    
    @GetMapping("/appointments/new")
    public String showCreateAppointmentForm(@RequestParam(required = false) Long childId, Model model) {
        Appointment appointment = new Appointment();
        
        // Nếu có childId, thiết lập liên kết với trẻ
        if (childId != null) {
            try {
                Child child = childService.getChildById(childId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ trẻ em"));
                appointment.setChild(child);
            } catch (Exception e) {
                model.addAttribute("error", "Không tìm thấy hồ sơ trẻ em: " + e.getMessage());
            }
        }
        
        model.addAttribute("appointment", appointment);
        model.addAttribute("children", childService.getAllChildren());
        model.addAttribute("vaccines", vaccineService.getAllVaccines());
        model.addAttribute("packages", vaccinePackageService.getAllPackages());
        
        return "admin/appointments/appointment-form";
    }
    
    @GetMapping("/appointments/edit/{id}")
    public String showEditAppointmentForm(@PathVariable Long id, Model model) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            model.addAttribute("appointment", appointment);
            model.addAttribute("children", childService.getAllChildren());
            model.addAttribute("vaccines", vaccineService.getAllVaccines());
            model.addAttribute("packages", vaccinePackageService.getAllPackages());
            
            return "admin/appointments/appointment-form";
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải thông tin đặt lịch: " + e.getMessage());
            return "redirect:/admin/appointments";
        }
    }
    
    @PostMapping("/appointments/save")
    public String saveAppointment(
        @ModelAttribute Appointment appointment,
        @RequestParam(value = "childId", required = false) Long childId,
        @RequestParam(value = "vaccineId", required = false) Long vaccineId,
        @RequestParam(value = "packageId", required = false) Long packageId,
        RedirectAttributes redirectAttributes) {
        
        try {
            // Liên kết với trẻ
            if (childId != null) {
                Child child = childService.getChildById(childId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ trẻ em"));
                appointment.setChild(child);
            }
            
            // Liên kết với vaccine hoặc gói
            if (vaccineId != null) {
                Vaccine vaccine = vaccineService.getVaccineById(vaccineId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy vaccine"));
                appointment.setVaccine(vaccine);
                // Xóa gói nếu đã chọn vaccine
                appointment.setVaccinePackage(null);
            } else if (packageId != null) {
                VaccinePackage vaccinePackage = vaccinePackageService.getPackageById(packageId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy gói vaccine"));
                appointment.setVaccinePackage(vaccinePackage);
                // Xóa vaccine nếu đã chọn gói
                appointment.setVaccine(null);
            }
            
            if (appointment.getId() == null) {
                appointmentService.saveAppointment(appointment);
                redirectAttributes.addFlashAttribute("message", "Tạo lịch hẹn thành công");
            } else {
                appointmentService.saveAppointment(appointment);
                redirectAttributes.addFlashAttribute("message", "Cập nhật lịch hẹn thành công");
            }
            
            return "redirect:/admin/appointments";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu lịch hẹn: " + e.getMessage());
            return "redirect:/admin/appointments";
        }
    }
    
    @PostMapping("/appointments/delete/{id}")
    public String deleteAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointment(id);
            redirectAttributes.addFlashAttribute("message", "Hủy lịch hẹn thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi hủy lịch hẹn: " + e.getMessage());
        }
        return "redirect:/admin/appointments";
    }

    @GetMapping("/children")
    public String children(Model model) {
        try {
            List<Child> children = childService.getAllChildren();
            model.addAttribute("children", children);
            
            // Debug thông tin về phụ huynh
            List<User> parents = userService.getUsersByRole("ROLE_PARENT");
            model.addAttribute("debug_parent_count", parents.size());
            
            for (Child child : children) {
                if (child.getParent() == null) {
                    System.out.println("Child " + child.getId() + " - " + child.getName() + " has NULL parent");
                    
                    // Nếu có parentUsername nhưng parent là null, thử liên kết lại
                    if (child.getParentUsername() != null && !child.getParentUsername().isEmpty()) {
                        System.out.println("Attempting to re-link parent with username: " + child.getParentUsername());
                        try {
                            User parent = userService.getUserByUsername(child.getParentUsername());
                            child.setParent(parent);
                            childService.saveChild(child);
                            System.out.println("Re-linked parent successfully");
                        } catch (Exception ex) {
                            System.out.println("Error re-linking parent: " + ex.getMessage());
                        }
                    }
                } else {
                    System.out.println("Child " + child.getId() + " - " + child.getName() + " has parent: " + 
                                       child.getParent().getName() + " (" + child.getParent().getUsername() + ")");
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải danh sách trẻ em: " + e.getMessage());
            model.addAttribute("children", List.of()); // Trả về danh sách rỗng
        }
        return "admin/children/children";
    }

    @GetMapping("/children/{id}")
    public String getChildById(@PathVariable Long id, Model model) {
        try {
            Child child = childService.getChildById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ trẻ em"));
            model.addAttribute("child", child);
            
            // Lấy danh sách lịch tiêm của trẻ
            List<Appointment> appointments = appointmentService.getAppointmentsByChildId(id);
            model.addAttribute("appointments", appointments);
            
            return "admin/children/child-details";
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải thông tin trẻ em: " + e.getMessage());
            return "redirect:/admin/children";
        }
    }

    @GetMapping("/children/new")
    public String showCreateChildForm(Model model) {
        model.addAttribute("child", new Child());
        
        // Lấy danh sách người dùng là phụ huynh để liên kết với trẻ
        try {
            // Thử tìm với các biến thể prefix ROLE_ khác nhau
            List<User> parents = new ArrayList<>();
            
            // Biến thể 1: ROLE_PARENT
            parents.addAll(userService.getUsersByRole("ROLE_PARENT"));
            
            // Biến thể 2: PARENT (không có ROLE_)
            parents.addAll(userService.getUsersByRole("PARENT"));
            
            // Debug info
            System.out.println("Found " + parents.size() + " parents");
            for (User parent : parents) {
                System.out.println("Parent: " + parent.getId() + " - " + parent.getUsername() + " - " + parent.getName() + " - Role: " + parent.getRole());
            }
            
            // Nếu vẫn không tìm thấy phụ huynh nào, lấy tất cả người dùng làm fallback
            if (parents.isEmpty()) {
                System.out.println("No parents found, fallback to all users");
                parents = userService.getAllUsers();
            }
            
            model.addAttribute("parents", parents);
        } catch (Exception e) {
            System.err.println("Error loading parents: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Không thể tải danh sách phụ huynh: " + e.getMessage());
            model.addAttribute("parents", List.of()); // Trả về danh sách rỗng
        }
        
        return "admin/children/child-form";
    }

    @GetMapping("/children/edit/{id}")
    public String showEditChildForm(@PathVariable Long id, Model model) {
        try {
            Child child = childService.getChildById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ trẻ em"));
            model.addAttribute("child", child);
            
            // Lấy danh sách người dùng là phụ huynh để liên kết với trẻ
            try {
                // Thử tìm với các biến thể prefix ROLE_ khác nhau
                List<User> parents = new ArrayList<>();
                
                // Biến thể 1: ROLE_PARENT
                parents.addAll(userService.getUsersByRole("ROLE_PARENT"));
                
                // Biến thể 2: PARENT (không có ROLE_)
                parents.addAll(userService.getUsersByRole("PARENT"));
                
                // Debug info
                System.out.println("Found " + parents.size() + " parents for edit form");
                
                // Nếu vẫn không tìm thấy phụ huynh nào, lấy tất cả người dùng làm fallback
                if (parents.isEmpty()) {
                    System.out.println("No parents found, fallback to all users");
                    parents = userService.getAllUsers();
                }
                
                model.addAttribute("parents", parents);
            } catch (Exception e) {
                System.err.println("Error loading parents for edit form: " + e.getMessage());
                model.addAttribute("error", "Không thể tải danh sách phụ huynh: " + e.getMessage());
                model.addAttribute("parents", List.of()); // Trả về danh sách rỗng
            }
            
            return "admin/children/child-form";
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải thông tin trẻ em: " + e.getMessage());
            return "redirect:/admin/children";
        }
    }

    @PostMapping("/children/save")
    public String saveChild(
        @ModelAttribute Child child,
        @RequestParam(value = "parentId", required = false) Long parentId,
        RedirectAttributes redirectAttributes) {
        
        try {
            // Nếu có parentId, liên kết trẻ với phụ huynh
            if (parentId != null) {
                User parent = userService.getUserById(parentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin phụ huynh"));
                child.setParent(parent);
                child.setParentUsername(parent.getUsername());
                
                // Debug info
                System.out.println("Setting parent for child " + child.getName() + ": " 
                    + parent.getId() + " - " + parent.getUsername());
            } else {
                // Nếu không có parentId, đảm bảo xóa bỏ liên kết phụ huynh
                child.setParent(null);
                child.setParentUsername(null);
                System.out.println("Clearing parent relationship for child " + child.getName());
            }
            
            if (child.getId() == null) {
                childService.saveChild(child);
                redirectAttributes.addFlashAttribute("message", "Tạo hồ sơ trẻ em thành công");
            } else {
                // Sử dụng saveChild thay vì updateChild để đảm bảo mối quan hệ được lưu đúng
                childService.saveChild(child);
                redirectAttributes.addFlashAttribute("message", "Cập nhật hồ sơ trẻ em thành công");
            }
            
            return "redirect:/admin/children";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu hồ sơ trẻ em: " + e.getMessage());
            return "redirect:/admin/children";
        }
    }

    @PostMapping("/children/delete/{id}")
    public String deleteChild(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            childService.deleteChild(id);
            redirectAttributes.addFlashAttribute("message", "Xóa hồ sơ trẻ em thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa hồ sơ trẻ em: " + e.getMessage());
        }
        return "redirect:/admin/children";
    }
    
    private byte[] compressImage(byte[] imageData) throws IOException {
        BufferedImage sourceImage = ImageIO.read(new ByteArrayInputStream(imageData));
        
        // Scale the image if it's too large
        int maxWidth = 800;
        int maxHeight = 800;
        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();
        
        if (width > maxWidth || height > maxHeight) {
            float ratio = (float) width / (float) height;
            if (ratio > 1) {
                width = maxWidth;
                height = (int) (width / ratio);
            } else {
                height = maxHeight;
                width = (int) (height * ratio);
            }
            
            BufferedImage resizedImage = new BufferedImage(width, height, sourceImage.getType());
            java.awt.Graphics2D g = resizedImage.createGraphics();
            g.drawImage(sourceImage, 0, 0, width, height, null);
            g.dispose();
            sourceImage = resizedImage;
        }
        
        // Compress the image
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(sourceImage, "jpeg", outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Update payment status
     */
    @PostMapping("/payments/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Payment payment = paymentService.updatePaymentStatus(id, status, null);
            
            if (payment != null) {
                response.put("success", true);
                response.put("message", "Trạng thái thanh toán đã được cập nhật thành: " + status);
                response.put("paymentId", payment.getId());
                response.put("status", payment.getStatus());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin thanh toán");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Error updating payment status: ", e);
            response.put("success", false);
            response.put("message", "Lỗi cập nhật trạng thái: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}