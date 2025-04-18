package edu.uth.childvaccinesystem.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMsg = "";
        String viewName = "error/general-error"; // Trang lỗi chung

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                errorMsg = "Page Not Found";
                viewName = "error/404"; // Trỏ đến templates/error/404.html
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorMsg = "Internal Server Error";
                viewName = "error/500"; // Trỏ đến templates/error/500.html
            }
            else if(statusCode == HttpStatus.FORBIDDEN.value()) {
                errorMsg = "Access Denied / Forbidden";
                viewName = "error/403"; // Trỏ đến templates/error/403.html
            } else {
                 errorMsg = "An unexpected error occurred";
            }
             model.addAttribute("statusCode", statusCode);
             model.addAttribute("errorMessage", errorMsg);
        } else {
             model.addAttribute("errorMessage", "An unexpected error occurred");
        }


        // Bạn có thể thêm các thông tin lỗi khác vào model nếu cần
        // model.addAttribute("timestamp", request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
        // model.addAttribute("exception", request.getAttribute(RequestDispatcher.ERROR_EXCEPTION));

        return viewName; // Trả về tên của view template (ví dụ: "error/403")
    }

    // @Override // Gỡ comment nếu dùng Spring Boot cũ hơn
    // public String getErrorPath() {
    //     return "/error";
    // }
}

