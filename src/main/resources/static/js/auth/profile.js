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
                <img src="data:image/jpeg;base64,${user.data}" alt="Profile Image" class="img-fluid rounded-circle mt-2" width="100">
                <p><strong>Username:</strong> ${user.username}</p>
                <p><strong>Name:</strong> ${user.name}</p>
                <p><strong>Phone:</strong> ${user.phone}</p>
                <p><strong>Email:</strong> ${user.email}</p>
                <p><strong>Address:</strong> ${user.address}</p>
                <p><strong>Role:</strong> ${user.role}</p>
            `;

            // Điền dữ liệu vào form
            document.getElementById('name').value = user.name || '';
            document.getElementById('phone').value = user.phone || '';
            document.getElementById('email').value = user.email || '';
            document.getElementById('address').value = user.address || '';

        } else {
            profileInfo.innerHTML = `<p class="text-danger">Không thể tải thông tin người dùng.</p>`;
        }
    } catch (error) {
        console.error('Lỗi khi tải thông tin:', error);
        profileInfo.innerHTML = `<p class="text-danger">Đã xảy ra lỗi khi tải thông tin hồ sơ.</p>`;
    }
}

loadProfile();

document.addEventListener("DOMContentLoaded", function () {
    const updateBtn = document.getElementById("updateProfileButton");
    const updateOverlay = document.getElementById("updateProfileOverlay");
    const closeBtn = document.getElementById("closeUpdateForm");

    updateBtn.addEventListener("click", function () {
        updateOverlay.style.display = "flex";
    });

    closeBtn.addEventListener("click", function () {
        updateOverlay.style.display = "none";
    });

    updateOverlay.addEventListener("click", function (e) {
        if (e.target === updateOverlay) {
            updateOverlay.style.display = "none";
        }
    });

    document.getElementById('updateProfileForm').addEventListener('submit', async function (e) {
        e.preventDefault();

        const formData = new FormData();
        formData.append('name', document.getElementById('name').value);
        formData.append('phone', document.getElementById('phone').value);
        formData.append('email', document.getElementById('email').value);
        formData.append('address', document.getElementById('address').value);

        const imageFile = document.getElementById('image').files[0];
        if (imageFile) {
            formData.append('image', imageFile);
        }

        try {
            const response = await fetch('/auths/update-profile', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                },
                body: formData
            });

            const messageDiv = document.getElementById('updateMessage');
            if (response.ok) {
                messageDiv.innerHTML = '<p class="text-success">Cập nhật thành công!</p>';
                updateOverlay.style.display = 'none';
                loadProfile(); // Reload
            } else {
                const errorText = await response.text();
                messageDiv.innerHTML = `<p class="text-danger">${errorText}</p>`;
            }
        } catch (error) {
            console.error('Lỗi cập nhật:', error);
            document.getElementById('updateMessage').innerHTML = '<p class="text-danger">Đã xảy ra lỗi khi cập nhật.</p>';
        }
    });
});
