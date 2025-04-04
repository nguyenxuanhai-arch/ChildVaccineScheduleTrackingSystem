document.getElementById('loginForm').onsubmit = async function(event) {
    event.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const errorMessage = document.getElementById('errorMessage');

    try {
        const response = await fetch('http://localhost:8080/auths/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const result = await response.json();
            localStorage.setItem('token', result.token); // Save token to localStorage
            window.location.href = '/'; // Redirect to profile page
        } else {
            errorMessage.style.display = 'block'; // Show error if login fails
        }
    } catch (error) {
        console.error('Error:', error);
        errorMessage.style.display = 'block'; // Show error if something goes wrong
    }
};