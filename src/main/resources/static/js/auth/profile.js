async function loadProfile() {
    const profileInfo = document.getElementById('profileInfo');

    try {
        const response = await fetch('/auths/profile', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        });

        if (response.ok) {
            const user = await response.json();
            profileInfo.innerHTML = `
                <p><strong>Username:</strong> ${user.username}</p>
                <p><strong>Name:</strong> ${user.name}</p>
                <p><strong>Phone:</strong> ${user.phone}</p>
                <p><strong>Role:</strong> ${user.role}</p>
                <img src="data:image/jpeg;base64,${user.image}" alt="Profile Image" class="img-fluid rounded-circle mt-2" width="100">
            `;
        } else {
            profileInfo.innerHTML = `<p class="text-danger">Failed to load profile information.</p>`;
        }
    } catch (error) {
        console.error('Error:', error);
        profileInfo.innerHTML = `<p class="text-danger">An error occurred while loading profile information.</p>`;
    }
}

// Load profile information when the page loads
loadProfile();
