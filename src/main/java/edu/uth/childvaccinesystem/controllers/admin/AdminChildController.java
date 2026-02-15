package edu.uth.childvaccinesystem.controllers.admin;

import edu.uth.childvaccinesystem.entities.Appointment;
import edu.uth.childvaccinesystem.entities.Child;
import edu.uth.childvaccinesystem.entities.User;
import edu.uth.childvaccinesystem.services.impl.AppointmentService;
import edu.uth.childvaccinesystem.services.impl.ChildService;
import edu.uth.childvaccinesystem.services.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/children")
public class AdminChildController {

    private static final Logger logger = LoggerFactory.getLogger(AdminChildController.class);

    @Autowired
    private ChildService childService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("")
    public String children(Model model) {
        try {
            List<Child> children = childService.getAllChildren();
            model.addAttribute("children", children);
            
            // Count unique parents from children list
            long parentCount = children.stream()
                .filter(child -> child.getParent() != null)
                .map(child -> child.getParent().getId())
                .distinct()
                .count();
            
            model.addAttribute("debug_parent_count", parentCount);
            
            // Re-link parents if needed
            for (Child child : children) {
                if (child.getParent() == null && child.getParentUsername() != null && !child.getParentUsername().isEmpty()) {
                    logger.info("Attempting to re-link parent with username: {}", child.getParentUsername());
                    try {
                        User parent = userService.getUserByUsername(child.getParentUsername());
                        if (parent != null) {
                            child.setParent(parent);
                            childService.saveChild(child);
                            logger.info("Re-linked parent successfully");
                        }
                    } catch (Exception ex) {
                        logger.error("Error re-linking parent: {}", ex.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "Cannot load children list: " + e.getMessage());
            model.addAttribute("children", List.of()); // Return empty list
            model.addAttribute("debug_parent_count", 0);
        }
        return "admin/children/children";
    }

    @GetMapping("/{id}")
    public String getChildById(@PathVariable Long id, Model model) {
        try {
            Child child = childService.getChildById(id)
                .orElseThrow(() -> new RuntimeException("Child profile not found"));
            model.addAttribute("child", child);
            
            // Get child's appointment history
            List<Appointment> appointments = appointmentService.getAppointmentsByChildId(id);
            model.addAttribute("appointments", appointments);
            
            return "admin/children/child-details";
        } catch (Exception e) {
            model.addAttribute("error", "Cannot load child information: " + e.getMessage());
            return "redirect:/admin/children";
        }
    }

    @GetMapping("/new")
    public String showCreateChildForm(Model model) {
        model.addAttribute("child", new Child());
        
        // Get list of parent users to link with the child
        try {
            // Try with different ROLE_ prefix variants
            List<User> parents = new ArrayList<>();
            
            // Variant 1: ROLE_PARENT
            parents.addAll(userService.getUsersByRole("ROLE_PARENT"));
            
            // Variant 2: PARENT (without ROLE_ prefix)
            parents.addAll(userService.getUsersByRole("PARENT"));
            
            // Debug info
            logger.info("Found {} parents", parents.size());
            
            // If no parents found, fall back to all users
            if (parents.isEmpty()) {
                logger.info("No parents found, falling back to all users");
                parents = userService.getAllUsers();
            }
            
            model.addAttribute("parents", parents);
        } catch (Exception e) {
            logger.error("Error loading parents: {}", e.getMessage());
            model.addAttribute("error", "Cannot load parent list: " + e.getMessage());
            model.addAttribute("parents", List.of()); // Return empty list
        }
        
        return "admin/children/child-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditChildForm(@PathVariable Long id, Model model) {
        try {
            Child child = childService.getChildById(id)
                .orElseThrow(() -> new RuntimeException("Child profile not found"));
            model.addAttribute("child", child);
            
            // Get list of parent users to link with the child
            try {
                // Try with different ROLE_ prefix variants
                List<User> parents = new ArrayList<>();
                
                // Variant 1: ROLE_PARENT
                parents.addAll(userService.getUsersByRole("ROLE_PARENT"));
                
                // Variant 2: PARENT (without ROLE_ prefix)
                parents.addAll(userService.getUsersByRole("PARENT"));
                
                // Debug info
                logger.info("Found {} parents for edit form", parents.size());
                
                // If no parents found, fall back to all users
                if (parents.isEmpty()) {
                    logger.info("No parents found, falling back to all users");
                    parents = userService.getAllUsers();
                }
                
                model.addAttribute("parents", parents);
            } catch (Exception e) {
                logger.error("Error loading parents for edit form: {}", e.getMessage());
                model.addAttribute("error", "Cannot load parent list: " + e.getMessage());
                model.addAttribute("parents", List.of()); // Return empty list
            }
            
            return "admin/children/child-form";
        } catch (Exception e) {
            model.addAttribute("error", "Cannot load child information: " + e.getMessage());
            return "redirect:/admin/children";
        }
    }

    @PostMapping("/save")
    public String saveChild(
        @ModelAttribute Child child,
        @RequestParam(value = "parentId", required = false) Long parentId,
        RedirectAttributes redirectAttributes) {
        
        try {
            // If parentId is provided, link child with parent
            if (parentId != null) {
                User parent = userService.getUserById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
                child.setParent(parent);
                child.setParentUsername(parent.getUsername());
                
                // Debug info
                logger.info("Setting parent for child {}: {} - {}", 
                    child.getName(), parent.getId(), parent.getUsername());
            } else {
                // If no parentId, clear parent relationship
                child.setParent(null);
                child.setParentUsername(null);
                logger.info("Clearing parent relationship for child {}", child.getName());
            }
            
            if (child.getId() == null) {
                childService.saveChild(child);
                redirectAttributes.addFlashAttribute("message", "Child profile created successfully");
            } else {
                // Use saveChild instead of updateChild to ensure relationships are saved correctly
                childService.saveChild(child);
                redirectAttributes.addFlashAttribute("message", "Child profile updated successfully");
            }
            
            return "redirect:/admin/children";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving child profile: " + e.getMessage());
            return "redirect:/admin/children";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteChild(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            childService.deleteChild(id);
            redirectAttributes.addFlashAttribute("message", "Child profile deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting child profile: " + e.getMessage());
        }
        return "redirect:/admin/children";
    }
}