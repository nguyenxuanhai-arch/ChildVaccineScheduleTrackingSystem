package edu.uth.childvaccinesystem.controllers.auth;

import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.services.AppointmentService;
import edu.uth.childvaccinesystem.services.ChildService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private ChildService childService;
    
    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private edu.uth.childvaccinesystem.services.UserService userService;

    @GetMapping("/register")
    public String registerUser() {
        return "auth/register"; // Ensure this maps to the correct template path
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login"; // Ensure this maps to the correct template path
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        // Get current logged in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        // Get user's children
        List<Child> children = childService.getChildrenByParentUsername(username);
        model.addAttribute("children", children);
        
        return "auth/profile"; // Đường dẫn đến file profile.html
    }
    
    @GetMapping("/profile/children")
    public String listChildren(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        List<Child> children = childService.getChildrenByParentUsername(username);
        
        // Synchronize appointments for each child
        for (Child child : children) {
            appointmentService.syncChildAppointments(child);
        }
        
        model.addAttribute("children", children);
        
        return "auth/children";
    }
    
    // REST API endpoint to get children list for the authenticated user
    @GetMapping("/profile/children/list")
    @ResponseBody
    public List<Child> getChildrenList() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        List<Child> children = childService.getChildrenByParentUsername(username);
        
        // Synchronize appointments for each child
        for (Child child : children) {
            appointmentService.syncChildAppointments(child);
        }
        
        return children;
    }
    
    @GetMapping("/profile/children/{id}")
    public String viewChild(@PathVariable Long id, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            Optional<Child> childOpt = childService.getChildById(id);
            if (childOpt.isEmpty()) {
                return "redirect:/auth/profile?error=child-not-found";
            }
            
            Child child = childOpt.get();
            // Verify the child belongs to the current user
            if (!child.getParentUsername().equals(username) && 
                (child.getParent() == null || !child.getParent().getUsername().equals(username))) {
                return "redirect:/auth/profile?error=access-denied";
            }
            
            // Use a simplified version of the child without loading the appointments
            // This avoids potential circular reference issues
            Child simplifiedChild = new Child();
            simplifiedChild.setId(child.getId());
            simplifiedChild.setName(child.getName());
            simplifiedChild.setDob(child.getDob());
            simplifiedChild.setGender(child.getGender());
            simplifiedChild.setParentUsername(child.getParentUsername());
            
            // Get appointments separately
            List<Appointment> childAppointments = appointmentService.getAppointmentsByChildId(id);
            System.out.println("Found " + childAppointments.size() + " appointments for child ID: " + id);
            
            model.addAttribute("child", simplifiedChild);
            model.addAttribute("appointments", childAppointments);
            
            return "auth/child-view";
        } catch (Exception e) {
            // Log the exception to help with debugging
            e.printStackTrace();
            System.err.println("Error in viewChild: " + e.getMessage());
            return "redirect:/auth/profile?error=view-error";
        }
    }
    
    @GetMapping("/profile/children/{id}/edit")
    public String editChildForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<Child> childOpt = childService.getChildById(id);
        if (childOpt.isEmpty()) {
            return "redirect:/auth/profile?error=child-not-found";
        }
        
        Child child = childOpt.get();
        // Verify the child belongs to the current user
        if (!child.getParentUsername().equals(username) && 
            (child.getParent() == null || !child.getParent().getUsername().equals(username))) {
            return "redirect:/auth/profile?error=access-denied";
        }
        
        model.addAttribute("child", child);
        return "auth/child-edit";
    }
    
    @PostMapping("/profile/children/{id}/update")
    public String updateChild(@PathVariable Long id, @ModelAttribute Child childDetails, 
                             RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<Child> childOpt = childService.getChildById(id);
        if (childOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy hồ sơ trẻ");
            return "redirect:/auth/profile";
        }
        
        Child child = childOpt.get();
        // Verify the child belongs to the current user
        if (!child.getParentUsername().equals(username) && 
            (child.getParent() == null || !child.getParent().getUsername().equals(username))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền chỉnh sửa hồ sơ này");
            return "redirect:/auth/profile";
        }
        
        try {
            // Preserve parent and parentUsername
            childDetails.setParent(child.getParent());
            childDetails.setParentUsername(child.getParentUsername());
            
            childService.updateChild(id, childDetails);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hồ sơ trẻ thành công");
            return "redirect:/auth/profile/children/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi cập nhật: " + e.getMessage());
            return "redirect:/auth/profile/children/" + id + "/edit";
        }
    }
    
    @GetMapping("/profile/children/add")
    public String addChildForm(Model model) {
        model.addAttribute("child", new Child());
        return "auth/child-add";
    }
    
    @PostMapping("/profile/children/add")
    public String addChild(@ModelAttribute Child child, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        try {
            child.setParentUsername(username);
            childService.saveChild(child);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm hồ sơ trẻ thành công");
            return "redirect:/auth/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi thêm hồ sơ: " + e.getMessage());
            return "redirect:/auth/profile/children/add";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }
    
    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "auth/change-password";
    }
    
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        // Validate input
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới và xác nhận mật khẩu không khớp");
            return "redirect:/auth/change-password";
        }
        
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự");
            return "redirect:/auth/change-password";
        }
        
        // Get current logged in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        try {
            userService.changePassword(username, currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("success", "Mật khẩu đã được thay đổi thành công");
            return "redirect:/auth/profile";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng");
            return "redirect:/auth/change-password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/auth/change-password";
        }
    }
}
