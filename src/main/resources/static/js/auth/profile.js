async function loadProfile() {
    const profileInfo = document.getElementById('profileInfo');
    if (!profileInfo) {
        console.log('Profile info element not found on this page');
        return;
    }

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
                <img src="data:image/jpeg;base64,${user.data}" alt="Profile Image" class="img-fluid rounded-circle mt-2" width="100">
                <p><strong>Username:</strong> ${user.username}</p>
                <p><strong>Name:</strong> ${user.name}</p>
                <p><strong>Phone:</strong> ${user.phone}</p>
                <p><strong>Email:</strong> ${user.email}</p>
                <p><strong>Address:</strong> ${user.address}</p>
                <p><strong>Role:</strong> ${user.role}</p>
            `;

            // Điền dữ liệu vào form nếu các phần tử tồn tại
            const nameField = document.getElementById('name');
            const phoneField = document.getElementById('phone');
            const emailField = document.getElementById('email');
            const addressField = document.getElementById('address');
            
            if (nameField) nameField.value = user.name || '';
            if (phoneField) phoneField.value = user.phone || '';
            if (emailField) emailField.value = user.email || '';
            if (addressField) addressField.value = user.address || '';

        } else {
            profileInfo.innerHTML = `<p class="text-danger">Không thể tải thông tin người dùng.</p>`;
        }
    } catch (error) {
        console.error('Lỗi khi tải thông tin:', error);
        if (profileInfo) {
            profileInfo.innerHTML = `<p class="text-danger">Đã xảy ra lỗi khi tải thông tin hồ sơ.</p>`;
        }
    }
}

// Cố gắng tải hồ sơ, nhưng chỉ khi cần thiết
if (document.getElementById('profileInfo')) {
    loadProfile();
}

document.addEventListener("DOMContentLoaded", function () {
    const updateBtn = document.getElementById("updateProfileButton");
    const updateOverlay = document.getElementById("updateProfileOverlay");
    const closeBtn = document.getElementById("closeUpdateForm");
    const updateForm = document.getElementById('updateProfileForm');

    // Chỉ thêm các sự kiện nếu các phần tử tồn tại
    if (updateBtn && updateOverlay) {
        updateBtn.addEventListener("click", function () {
            updateOverlay.style.display = "flex";
        });
    }

    if (closeBtn && updateOverlay) {
        closeBtn.addEventListener("click", function () {
            updateOverlay.style.display = "none";
        });
    }

    if (updateOverlay) {
        updateOverlay.addEventListener("click", function (e) {
            if (e.target === updateOverlay) {
                updateOverlay.style.display = "none";
            }
        });
    }

    if (updateForm) {
        updateForm.addEventListener('submit', async function (e) {
            e.preventDefault();

            const formData = new FormData();
            const nameField = document.getElementById('name');
            const phoneField = document.getElementById('phone');
            const emailField = document.getElementById('email');
            const addressField = document.getElementById('address');
            const imageField = document.getElementById('image');
            const messageDiv = document.getElementById('updateMessage');
            
            if (nameField) formData.append('name', nameField.value);
            if (phoneField) formData.append('phone', phoneField.value);
            if (emailField) formData.append('email', emailField.value);
            if (addressField) formData.append('address', addressField.value);

            if (imageField && imageField.files && imageField.files[0]) {
                formData.append('image', imageField.files[0]);
            }

            try {
                const response = await fetch('/auths/update-profile', {
                    method: 'POST',
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('token')
                    },
                    body: formData
                });

                if (messageDiv) {
                    if (response.ok) {
                        messageDiv.innerHTML = '<p class="text-success">Cập nhật thành công!</p>';
                        if (updateOverlay) updateOverlay.style.display = 'none';
                        // Chỉ tải lại hồ sơ nếu phần tử profileInfo tồn tại
                        if (document.getElementById('profileInfo')) {
                            loadProfile();
                        }
                    } else {
                        const errorText = await response.text();
                        messageDiv.innerHTML = `<p class="text-danger">${errorText}</p>`;
                    }
                }
            } catch (error) {
                console.error('Lỗi cập nhật:', error);
                if (messageDiv) {
                    messageDiv.innerHTML = '<p class="text-danger">Đã xảy ra lỗi khi cập nhật.</p>';
                }
            }
        });
    }
});

async function loadNavbarAvatar() {
    const avatarImg = document.getElementById('navbarAvatar');
    if (!avatarImg) {
        console.log('Navbar avatar element not found on this page');
        return;
    }

    try {
        const response = await fetch('/auths/profile', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        });

        if (response.ok) {
            const user = await response.json();
            if (user.data) {
                avatarImg.src = `data:image/jpeg;base64,${user.data}`;
            } else {
                avatarImg.src = '/img/default-avatar.jpg'; // fallback nếu không có ảnh
            }
        } else {
            avatarImg.src = '/img/default-avatar.jpg';
        }
    } catch (error) {
        console.error("Lỗi tải avatar header:", error);
        if (avatarImg) {
            avatarImg.src = '/img/default-avatar.jpg';
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    // Chỉ tải avatar nếu phần tử avatar tồn tại
    if (document.getElementById('navbarAvatar')) {
        loadNavbarAvatar();
    }
});
