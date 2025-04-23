package edu.uth.childvaccinesystem.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class CustomErrorController implements ErrorController {
    
    private static final Logger logger = Logger.getLogger(CustomErrorController.class.getName());

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Get error attributes
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorException = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        
        // Default error information
        String errorMsg = "An unexpected error occurred";
        String viewName = "error/general-error"; // Default error page
        
        // Determine if user is accessing the admin section by checking the request URI
        boolean isAdminSection = requestUri != null && requestUri.toString().contains("/admin/");
        String section = isAdminSection ? "admin" : "user";
        
        // Log error details
        logger.log(Level.SEVERE, "[{0} Section] Error handling request to: {1}", new Object[]{section, requestUri});
        logger.log(Level.SEVERE, "Error message: {0}", errorMessage);
        
        if (errorException != null) {
            logger.log(Level.SEVERE, "Exception: ", (Throwable) errorException);
        }

        // Process status code to determine view and message
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            logger.log(Level.SEVERE, "Status code: {0}", statusCode);

            // Select appropriate view based on status code and section
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorMsg = "Page Not Found";
                viewName = isAdminSection ? "error/admin/404" : "error/404";
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorMsg = "Internal Server Error";
                viewName = isAdminSection ? "error/admin/500" : "error/500";
            }
            else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                errorMsg = "Access Denied / Forbidden";
                viewName = isAdminSection ? "error/admin/403" : "error/403";
            }
            else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                errorMsg = "Authentication Required";
                viewName = isAdminSection ? "error/admin/401" : "error/401";
                
                // Check if should redirect to login page
                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute("user") == null) {
                    return isAdminSection ? "redirect:/admin/login" : "redirect:/auth/login";
                }
            }
            else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                errorMsg = "Bad Request";
                viewName = isAdminSection ? "error/admin/400" : "error/400";
            }
            else {
                // Generic error page for other status codes
                viewName = isAdminSection ? "error/admin/general-error" : "error/general-error";
            }
            
            // Add error details to model
            model.addAttribute("statusCode", statusCode);
            model.addAttribute("errorMessage", errorMsg);
            model.addAttribute("requestPath", requestUri);
            model.addAttribute("isAdminSection", isAdminSection);
            model.addAttribute("exceptionDetails", errorException != null ? errorException.toString() : "None");
        } else {
            model.addAttribute("errorMessage", errorMsg);
            model.addAttribute("isAdminSection", isAdminSection);
        }

        // Try to use the specific view, fall back to general error if template doesn't exist
        return viewName;
    }

    // For API error handling
    @RequestMapping("/api/error")
    public String handleApiError(HttpServletRequest request, Model model) {
        // Get error attributes
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorException = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        // Log error details
        logger.log(Level.SEVERE, "[API] Error handling request to: {0}", requestUri);
        
        if (errorException != null) {
            logger.log(Level.SEVERE, "API Exception: ", (Throwable) errorException);
        }
        
        // For API errors, we return a JSON response
        model.addAttribute("timestamp", System.currentTimeMillis());
        model.addAttribute("status", status != null ? Integer.valueOf(status.toString()) : 500);
        model.addAttribute("error", errorException != null ? errorException.toString() : "Unknown error");
        model.addAttribute("path", requestUri != null ? requestUri.toString() : "Unknown path");
        
        return "error/api-error"; // Will be handled by a @RestController advice
    }
}

