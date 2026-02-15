package edu.uth.childvaccinesystem.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private HttpStatus status;
    private LocalDateTime timestamp;
    private T data;
    private ErrorResponse error;
    private Map<String, String> errors;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorResponse {
        private String code;
        private String message;
        private String detail;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public ErrorResponse(String code, String message, String detail) {
            this.code = code;
            this.message = message;
            this.detail = detail;
        }
    }

    private ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public static <T> Builder <T> builder() {
        return new Builder<>();
    }
    public static class Builder<T> {
        private final ApiResponse<T> resource;

        public Builder() {
            resource = new ApiResponse<>();
        }

        public Builder<T> success(boolean success) {
            resource.success = success;
            return this;
        }

        public Builder<T> message(String message) {
            resource.message = message;
            return this;
        }

        public Builder<T> data(T data) {
            resource.data =data;
            return this;
        }

        public Builder<T> status(HttpStatus status) {
            resource.status = status;
            return this;
        }

        public Builder<T> error(ErrorResponse error) {
            resource.error = error;
            return this;
        }

        public ApiResponse<T> build() {
            return resource;
        }

        public Builder<T> errors(Map<String, String> errors) {
            resource.errors = errors;
            return this;
        }
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .status(HttpStatus.OK)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> message(String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .status(status)
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(new ErrorResponse(code, message))
                .status(status)
                .build();
    }

    public static <T> ApiResponse<T> errors(Map<String, String> errors, String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .status(status)
                .build();
    }

    public static <T> ApiResponse<T> errorDetail(String code, String message, String detail, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(new ErrorResponse(code, message, detail))
                .status(status)
                .build();
    }

    public static <T> ApiResponse<T> errorForObjectData(boolean success, String message, HttpStatus status, T data, Map<String,String> errors) {
        return ApiResponse.<T>builder()
                .success(success)
                .message(message)
                .status(status)
                .data(data)
                .errors(errors)
                .build();
    }
}
