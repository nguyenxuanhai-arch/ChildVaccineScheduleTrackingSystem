document.addEventListener("DOMContentLoaded", function () {
    const toggleButton = document.getElementById("darkModeToggle");
    const body = document.body;

    // Kiểm tra xem có Dark Mode được lưu trong Local Storage không
    if (localStorage.getItem("dark-mode") === "enabled") {
        body.classList.add("dark-mode");
    }

    // Xử lý khi bấm nút
    toggleButton.addEventListener("click", function () {
        body.classList.toggle("dark-mode");

        // Lưu trạng thái vào Local Storage
        if (body.classList.contains("dark-mode")) {
            localStorage.setItem("dark-mode", "enabled");
        } else {
            localStorage.setItem("dark-mode", "disabled");
        }
    });
});
