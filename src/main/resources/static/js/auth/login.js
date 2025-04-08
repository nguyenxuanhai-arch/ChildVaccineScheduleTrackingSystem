async function submitLogin() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const errorMessage = document.getElementById('errorMessage');

    try {
        const response = await fetch('/auths/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json")) {
                const result = await response.json();
                localStorage.setItem('token', result.token);
                successMessage.style.display = 'block';
                setTimeout(() => {
                    window.location.href = '/';
                }, 2000);
            } else {
                console.error("Không phải JSON:", await response.text()); // Xem nội dung thực tế
                errorMessage.style.display = 'block';
            }
        } else {
            errorMessage.style.display = 'block';
        }
    } catch (error) {
        console.error('Error:', error);
        errorMessage.style.display = 'block';
    }
}

  const toggle = document.getElementById("darkModeToggle");
  toggle.addEventListener("click", () => {
    document.body.classList.toggle("dark-mode");
    // Lưu vào localStorage nếu muốn nhớ chế độ
    localStorage.setItem("darkMode", document.body.classList.contains("dark-mode"));
  });

  // Tự động bật dark mode nếu đã bật trước đó
  window.addEventListener("DOMContentLoaded", () => {
    if (localStorage.getItem("darkMode") === "true") {
      document.body.classList.add("dark-mode");
    }
  });

