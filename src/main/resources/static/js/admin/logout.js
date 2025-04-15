// logout function
function logout() {
    fetch('/auth/logout', {
        method: 'POST',
        credentials: 'include'  // Gửi cookie cùng request
    }).finally(() => {
        window.location.href = '/admin/login';
    });
}

// Chạy khi trang được load
document.addEventListener('DOMContentLoaded', () => {
    // Không cần kiểm tra auth thủ công nữa, Spring Security sẽ tự xử lý
});
