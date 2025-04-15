    // Dark mode functionality
    document.addEventListener('DOMContentLoaded', function() {
        const darkModeToggle = document.getElementById('darkModeToggle');
        const body = document.body;
        
        // Check for saved dark mode preference
        const darkMode = localStorage.getItem('darkMode') === 'true';
        
        // Apply saved preference
        if (darkMode) {
            body.classList.add('dark-mode');
            darkModeToggle.textContent = '☀️';
        }
        
        // Toggle dark mode
        darkModeToggle.addEventListener('click', function() {
            body.classList.toggle('dark-mode');
            const isDarkMode = body.classList.contains('dark-mode');
            localStorage.setItem('darkMode', isDarkMode);
            this.textContent = isDarkMode ? '☀️' : '🌙';
        });
    });
