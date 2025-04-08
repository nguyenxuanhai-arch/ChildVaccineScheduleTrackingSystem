document.getElementById('registerForm').onsubmit = async function(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    const role = 'USER';
    const errorMessage = document.getElementById('errorMessage');
    const successMessage = document.getElementById('successMessage');
    
    // Reset messages
    errorMessage.style.display = 'none';
    successMessage.style.display = 'none';

    // Basic validation
    if (username.length < 3) {
        errorMessage.textContent = 'Username must be at least 3 characters long';
        errorMessage.style.display = 'block';
        return;
    }

    if (password.length < 6) {
        errorMessage.textContent = 'Password must be at least 6 characters long';
        errorMessage.style.display = 'block';
        return;
    }

    try {
        const response = await fetch('/auths/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password, role })
        });

        if (response.ok) {
            successMessage.style.display = 'block';
            setTimeout(() => {
                window.location.href = '/auth/login';
            }, 2000);
        } else {
            const data = await response.json();
            errorMessage.textContent = data.message || 'Registration failed. Please try again.';
            errorMessage.style.display = 'block';
        }
    } catch (error) {
        console.error('Error:', error);
        errorMessage.textContent = 'An error occurred. Please try again.';
        errorMessage.style.display = 'block';
    }
};

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
