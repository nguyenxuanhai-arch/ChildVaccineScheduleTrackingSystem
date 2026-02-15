package edu.uth.childvaccinesystem.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.dtos.request.AppointmentRequest;
import edu.uth.childvaccinesystem.dtos.request.PackageAppointmentRequest;
import edu.uth.childvaccinesystem.services.impl.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.stream.Collectors;
import edu.uth.childvaccinesystem.entities.Appointment.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.security.Principal;
import edu.uth.childvaccinesystem.entities.Feedback;
import edu.uth.childvaccinesystem.services.impl.FeedbackService;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping
    public String appointmentsPage() {
        return "appointments/appointments"; // đúng đường dẫn tới file HTML bạn để trong templates
    }
    
    @GetMapping("/history")
    public String appointmentHistory(Model model, 
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    @RequestParam(required = false) String tab,
                                    @RequestParam(required = false) String type) {
        // Lấy người dùng hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String username = auth.getName();
            System.out.println("Đang lấy danh sách lịch đặt cho username: " + username);
            
            // Lấy tất cả lịch đặt của người dùng
            List<Appointment> allAppointmentsList = appointmentService.getAppointmentsByUsername(username);
            
            // Sắp xếp theo ngày tạo giảm dần (mới nhất lên đầu)
            allAppointmentsList.sort((a1, a2) -> {
                if (a1.getCreateAt() == null) return 1;
                if (a2.getCreateAt() == null) return -1;
                return a2.getCreateAt().compareTo(a1.getCreateAt());
            });
            
            // Chuẩn bị các danh sách theo trạng thái
            List<Appointment> scheduledAppointmentsList = allAppointmentsList.stream()
                .filter(a -> a.getStatus() != null && AppointmentStatus.SCHEDULED.equals(a.getStatus()))
                .collect(Collectors.toList());
                
            List<Appointment> completedAppointmentsList = allAppointmentsList.stream()
                .filter(a -> a.getStatus() != null && AppointmentStatus.COMPLETED.equals(a.getStatus()))
                .collect(Collectors.toList());
                
            List<Appointment> cancelledAppointmentsList = allAppointmentsList.stream()
                .filter(a -> a.getStatus() != null && AppointmentStatus.CANCELLED.equals(a.getStatus()))
                .collect(Collectors.toList());
            
            // Lấy danh sách các ID lịch hẹn đã có đánh giá
            List<Long> ratedAppointmentIds = new ArrayList<>();
            Map<Long, Feedback> appointmentFeedbacks = new HashMap<>();
            
            // Lấy thông tin đánh giá cho các lịch hẹn đã hoàn thành
            for (Appointment appointment : completedAppointmentsList) {
                try {
                    Feedback feedback = feedbackService.getFeedbackByAppointmentId(appointment.getId());
                    if (feedback != null) {
                        ratedAppointmentIds.add(appointment.getId());
                        appointmentFeedbacks.put(appointment.getId(), feedback);
                    }
                } catch (Exception e) {
                    // Lịch hẹn chưa có đánh giá - không làm gì cả
                }
            }
            
            // Thêm thông tin đánh giá vào model
            model.addAttribute("ratedAppointmentIds", ratedAppointmentIds);
            model.addAttribute("appointmentFeedbacks", appointmentFeedbacks);
            
            // Xử lý phân trang dựa trên tab hiện tại
            if (tab != null && type != null) {
                switch (type) {
                    case "scheduled":
                        createPaginationForTab(model, scheduledAppointmentsList, "scheduledAppointments", page, size);
                        createSimplePageForTab(model, allAppointmentsList, "allAppointments", 0, size);
                        createSimplePageForTab(model, completedAppointmentsList, "completedAppointments", 0, size);
                        createSimplePageForTab(model, cancelledAppointmentsList, "cancelledAppointments", 0, size);
                        model.addAttribute("activeTab", "scheduled");
                        break;
                    case "completed":
                        createPaginationForTab(model, completedAppointmentsList, "completedAppointments", page, size);
                        createSimplePageForTab(model, allAppointmentsList, "allAppointments", 0, size);
                        createSimplePageForTab(model, scheduledAppointmentsList, "scheduledAppointments", 0, size);
                        createSimplePageForTab(model, cancelledAppointmentsList, "cancelledAppointments", 0, size);
                        model.addAttribute("activeTab", "completed");
                        break;
                    case "cancelled":
                        createPaginationForTab(model, cancelledAppointmentsList, "cancelledAppointments", page, size);
                        createSimplePageForTab(model, allAppointmentsList, "allAppointments", 0, size);
                        createSimplePageForTab(model, scheduledAppointmentsList, "scheduledAppointments", 0, size);
                        createSimplePageForTab(model, completedAppointmentsList, "completedAppointments", 0, size);
                        model.addAttribute("activeTab", "cancelled");
                        break;
                    default:
                        createPaginationForTab(model, allAppointmentsList, "allAppointments", page, size);
                        createSimplePageForTab(model, scheduledAppointmentsList, "scheduledAppointments", 0, size);
                        createSimplePageForTab(model, completedAppointmentsList, "completedAppointments", 0, size);
                        createSimplePageForTab(model, cancelledAppointmentsList, "cancelledAppointments", 0, size);
                        model.addAttribute("activeTab", "all");
                }
            } else {
                // Mặc định hiển thị tab "all" với phân trang
                createPaginationForTab(model, allAppointmentsList, "allAppointments", page, size);
                createSimplePageForTab(model, scheduledAppointmentsList, "scheduledAppointments", 0, size);
                createSimplePageForTab(model, completedAppointmentsList, "completedAppointments", 0, size);
                createSimplePageForTab(model, cancelledAppointmentsList, "cancelledAppointments", 0, size);
                model.addAttribute("activeTab", "all");
            }
            
            // Log thông tin
            System.out.println("Tổng số lịch đặt: " + allAppointmentsList.size());
            System.out.println("Số lịch đã đặt: " + scheduledAppointmentsList.size());
            System.out.println("Số lịch hoàn thành: " + completedAppointmentsList.size());
            System.out.println("Số lịch đã hủy: " + cancelledAppointmentsList.size());
            
            // Thêm thông tin tab và loại lọc vào model
            model.addAttribute("tabParam", tab);
            model.addAttribute("typeParam", type);
        }
        return "appointments/history";
    }

    private void createPaginationForTab(Model model, List<Appointment> appointmentsList, String attributeName, int page, int size) {
        int totalAppointments = appointmentsList.size();
        int start = Math.min(page * size, totalAppointments);
        int end = Math.min(start + size, totalAppointments);
        
        List<Appointment> pageContent = appointmentsList.isEmpty() ? appointmentsList : 
            appointmentsList.subList(start, end);
        
        Page<Appointment> appointmentsPage = new PageImpl<>(
            pageContent,
            PageRequest.of(page, size),
            totalAppointments
        );
        
        model.addAttribute(attributeName, appointmentsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appointmentsPage.getTotalPages());
        model.addAttribute("pageSize", size);
    }

    private void createSimplePageForTab(Model model, List<Appointment> appointmentsList, String attributeName, int page, int size) {
        int totalAppointments = appointmentsList.size();
        int start = Math.min(page * size, totalAppointments);
        int end = Math.min(start + size, totalAppointments);
        
        List<Appointment> pageContent = appointmentsList.isEmpty() ? appointmentsList : 
            appointmentsList.subList(start, end);
        
        Page<Appointment> appointmentsPage = new PageImpl<>(
            pageContent,
            PageRequest.of(page, size),
            totalAppointments
        );
        
        model.addAttribute(attributeName, appointmentsPage);
    }

    @PostMapping("/book")
    @ResponseBody
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request) {
        try {
            Appointment bookedAppointment = appointmentService.bookAppointment(request);
            return ResponseEntity.ok(
                java.util.Map.of(
                    "id", bookedAppointment.getId(),
                    "success", true,
                    "redirectUrl", "/payments/confirm/" + bookedAppointment.getId()
                )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                java.util.Map.of(
                    "success", false,
                    "message", e.getMessage()
                )
            );
        }
    }
    
    @PostMapping("/book-package")
    @ResponseBody
    public ResponseEntity<?> bookPackageAppointment(@RequestBody PackageAppointmentRequest request) {
        try {
            Appointment bookedAppointment = appointmentService.bookPackageAppointment(request);
            // Return the appointment id and redirect URL for client-side redirect
            return ResponseEntity.ok(
                java.util.Map.of(
                    "id", bookedAppointment.getId(),
                    "success", true,
                    "redirectUrl", "/payments/confirm/" + bookedAppointment.getId()
                )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                java.util.Map.of(
                    "success", false,
                    "message", e.getMessage()
                )
            );
        }
    }

    @PutMapping("/{id}/cancel")
    @ResponseBody
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        try {
            appointmentService.cancelAppointment(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().header("error", e.getMessage()).build();
        }
    }
    
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<?> getAllAppointments() {
        try {
            // Lấy người dùng hiện tại
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            List<Appointment> appointments;
            
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                String username = auth.getName();
                System.out.println("Đang lấy danh sách lịch đặt cho username: " + username);
                appointments = appointmentService.getAppointmentsByUsername(username);
                System.out.println("Tìm thấy " + appointments.size() + " lịch đặt.");
            } else {
                // Trả về tất cả nếu không có người dùng đăng nhập
                appointments = appointmentService.getAllAppointments();
                System.out.println("Người dùng chưa đăng nhập, trả về tất cả " + appointments.size() + " lịch đặt.");
            }
            
            // Trả về danh sách lịch đặt
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách lịch đặt: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                java.util.Map.of(
                    "success", false, 
                    "message", "Lỗi khi lấy danh sách lịch đặt: " + e.getMessage()
                )
            );
        }
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(appointmentService.getAppointmentById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/view/{id}")
    public String viewAppointmentDetails(@PathVariable Long id, Model model, Principal principal) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            
            // Kiểm tra nếu lịch hẹn không tồn tại
            if (appointment == null) {
                model.addAttribute("error", "Không tìm thấy lịch hẹn");
                return "redirect:/appointments/history";
            }
            
            // Kiểm tra quyền truy cập - chỉ cho phép xem lịch hẹn của chính mình
            String currentUsername = principal.getName();
            if (appointment.getChild() != null && appointment.getChild().getParent() != null 
                    && !appointment.getChild().getParentUsername().equals(currentUsername)) {
                model.addAttribute("error", "Bạn không có quyền xem lịch hẹn này");
                return "redirect:/appointments/history";
            }
            
            // Kiểm tra xem lịch hẹn có đánh giá chưa
            boolean hasRating = false;
            Feedback feedback = null;
            
            if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                try {
                    feedback = feedbackService.getFeedbackByAppointmentId(id);
                    hasRating = (feedback != null);
                } catch (Exception e) {
                    // Không có đánh giá - bỏ qua
                }
            }
            
            model.addAttribute("appointment", appointment);
            model.addAttribute("hasRating", hasRating);
            model.addAttribute("feedback", feedback);
            
            return "appointments/details";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/appointments/history";
        }
    }
}
