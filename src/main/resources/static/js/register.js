document.getElementById('registerForm').onsubmit = async function(event) {
            event.preventDefault();
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const role = 'USER';
            try {
                const response = await fetch('http://localhost:8080/auths/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ username, password, role})
                });

                if (response.ok) {
                    alert('Registration successful');
                    window.location.href = '/'; // Redirect to login page
                } else {
                    alert('Registration failed');
                }
            } catch (error) {
                console.error('Error:', error);
                alert('Registration failed');
            }
        };