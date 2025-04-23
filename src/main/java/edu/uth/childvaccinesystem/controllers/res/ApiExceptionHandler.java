package edu.uth.childvaccinesystem.controllers.res;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * Global exception handler for REST API endpoints
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Handle generic exceptions for API requests
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "An unexpected error occurred");
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Handle unauthorized access exceptions
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "Access denied");
        errorResponse.put("error", "You don't have permission to access this resource");
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    
    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            org.springframework.security.core.AuthenticationException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "Authentication failed");
        errorResponse.put("error", "Invalid credentials or session expired");
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    /**
     * Handle custom resource not found exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "Request failed");
        errorResponse.put("error", ex.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        // Determine HTTP status based on error message
        if (ex.getMessage() != null) {
            String errorMsg = ex.getMessage().toLowerCase();
            if (errorMsg.contains("not found") || errorMsg.contains("không tìm thấy")) {
                status = HttpStatus.NOT_FOUND;
            } else if (errorMsg.contains("permission") || errorMsg.contains("access denied") || 
                       errorMsg.contains("không có quyền")) {
                status = HttpStatus.FORBIDDEN;
            } else if (errorMsg.contains("invalid") || errorMsg.contains("không hợp lệ")) {
                status = HttpStatus.BAD_REQUEST;
            }
        }
        
        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * Converts ModelAndView to JSON response for error/api-error view
     */
    public ModelAndView handleErrorView(Map<String, Object> model) {
        ModelAndView mav = new ModelAndView();
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setPrettyPrint(true);
        mav.setView(view);
        mav.addAllObjects(model);
        return mav;
    }
} 