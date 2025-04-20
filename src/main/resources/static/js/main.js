    // Dark mode functionality and UI enhancements
    document.addEventListener('DOMContentLoaded', function() {
        // Dark Mode Toggle
        const darkModeToggle = document.getElementById('darkModeToggle');
        const body = document.body;
        
        // Check for saved dark mode preference
        const darkMode = localStorage.getItem('darkMode') === 'true';
        
        // Apply saved preference
        if (darkMode) {
            body.classList.add('dark-mode');
            darkModeToggle.innerHTML = '☀️';
        } else {
            darkModeToggle.innerHTML = '🌙';
        }
        
        // Toggle dark mode with animation
        darkModeToggle.addEventListener('click', function() {
            body.classList.toggle('dark-mode');
            const isDarkMode = body.classList.contains('dark-mode');
            localStorage.setItem('darkMode', isDarkMode);
            
            // Animated icon swap
            if (isDarkMode) {
                this.innerHTML = '☀️';
                this.classList.add('rotate-animation');
            } else {
                this.innerHTML = '🌙';
                this.classList.add('rotate-animation');
            }
            
            // Remove animation class after animation completes
            setTimeout(() => {
                this.classList.remove('rotate-animation');
            }, 500);
        });
        
        // Add animation class
        const style = document.createElement('style');
        style.textContent = `
            .rotate-animation {
                animation: rotate 0.5s ease;
            }
            @keyframes rotate {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
        `;
        document.head.appendChild(style);
        
        // Add smooth scroll behavior
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function(e) {
                e.preventDefault();
                const targetId = this.getAttribute('href');
                if (targetId === '#') return;
                
                const targetElement = document.querySelector(targetId);
                if (targetElement) {
                    window.scrollTo({
                        top: targetElement.offsetTop - 80, // Offset for navbar
                        behavior: 'smooth'
                    });
                }
            });
        });
        
        // Initialize tooltips if Bootstrap is loaded
        if (typeof bootstrap !== 'undefined' && bootstrap.Tooltip) {
            const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
            tooltipTriggerList.map(function(tooltipTriggerEl) {
                return new bootstrap.Tooltip(tooltipTriggerEl);
            });
        }
        
        // Add fade-in animation to sections on scroll
        const animateOnScroll = function() {
            const elements = document.querySelectorAll('.animate-on-scroll');
            elements.forEach(element => {
                const elementTop = element.getBoundingClientRect().top;
                const elementVisible = 150;
                
                if (elementTop < window.innerHeight - elementVisible) {
                    element.classList.add('visible');
                }
            });
        };
        
        // Apply fade-in class to sections
        document.querySelectorAll('.container > .row, .jumbotron, .card').forEach(element => {
            if (!element.closest('.navbar') && !element.closest('.footer')) {
                element.classList.add('animate-on-scroll');
            }
        });
        
        // Add scroll animation styles
        const scrollStyle = document.createElement('style');
        scrollStyle.textContent = `
            .animate-on-scroll {
                opacity: 0;
                transform: translateY(20px);
                transition: opacity 0.6s ease, transform 0.6s ease;
            }
            .animate-on-scroll.visible {
                opacity: 1;
                transform: translateY(0);
            }
        `;
        document.head.appendChild(scrollStyle);
        
        // Call once to check for elements in view on page load
        animateOnScroll();
        
        // Listen for scroll to trigger animations
        window.addEventListener('scroll', animateOnScroll);
    });
