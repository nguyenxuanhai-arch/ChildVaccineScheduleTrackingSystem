document.addEventListener("DOMContentLoaded", () => {
    loadVaccines();
});

let vaccineList = []; // Lưu danh sách vaccine để tìm kiếm

function loadVaccines() {
    fetch('/vaccine/vaccine-list')
        .then(response => response.json())
        .then(data => {
            vaccineList = data; // Lưu danh sách vaccine vào biến toàn cục
            renderVaccines(vaccineList); // Hiển thị danh sách vaccine
        })
        .catch(error => console.error('Lỗi khi tải danh sách vaccine:', error));
}

function renderVaccines(vaccines) {
    const cardsContainer = document.getElementById('vaccine-cards');
    cardsContainer.innerHTML = ''; // Xóa nội dung cũ

    vaccines.forEach(vaccine => {
        const card = document.createElement('div');
        card.className = 'col-md-4 mb-4';

        card.innerHTML = `
            <div class="card h-100">
                <img src="data:image/jpeg;base64,${vaccine.imageBase64}" class="card-img-top" alt="${vaccine.name}" style="height: 200px; object-fit: cover;">
                <div class="card-body">
                    <h5 class="card-title">${vaccine.name}</h5>
                    <p class="card-text">
                        <strong>Nhà sản xuất:</strong> ${vaccine.manufacturer}<br>
                        <strong>Số lô:</strong> ${vaccine.lotNumber}<br>
                        <strong>Hạn sử dụng:</strong> ${vaccine.expirationDate}<br>
                        <strong>Giá thành:</strong> ${vaccine.price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })}
                    </p>
                </div>
                <div class="card-footer text-center">
                    <button class="btn btn-warning btn-sm" onclick="editVaccine(${vaccine.id})">Sửa</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteVaccine(${vaccine.id})">Xóa</button>
                </div>
            </div>
        `;

        cardsContainer.appendChild(card);
    });
}

function showCreateForm() {
    document.getElementById('vaccineForm').reset(); // Reset form
    document.getElementById('vaccineId').value = ''; // Clear hidden ID field
    document.getElementById('modalTitle').textContent = 'Thêm Vaccine'; // Set modal title
    const modal = new bootstrap.Modal(document.getElementById('vaccineModal'));
    modal.show(); // Hiển thị modal
}

function saveVaccine() {
    const id = document.getElementById('vaccineId').value;
    const name = document.getElementById('name').value;
    const manufacturer = document.getElementById('manufacturer').value;
    const lotNumber = document.getElementById('lotNumber').value;
    const expirationDate = document.getElementById('expirationDate').value;
    const price = document.getElementById('price').value;
    const image = document.getElementById('image').files[0];

    if (image && image.size > 5 * 1024 * 1024) { // Giới hạn kích thước 5MB
        alert('Hình ảnh quá lớn. Vui lòng chọn hình ảnh nhỏ hơn 5MB.');
        return;
    }

    const formData = new FormData();
    formData.append('name', name);
    formData.append('manufacturer', manufacturer);
    formData.append('lotNumber', lotNumber);
    formData.append('expirationDate', expirationDate);
    formData.append('price', price);
    if (image) {
        formData.append('image', image);
    }

    const method = id ? 'PUT' : 'POST';
    const url = id ? `/vaccine/${id}` : '/vaccine';

    fetch(url, {
        method: method,
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                console.error('Lỗi phản hồi từ server:', response.status, response.statusText);
                throw new Error('Lỗi khi lưu vaccine');
            }
            return response.json();
        })
        .then(data => {
            console.log('Vaccine saved successfully:', data);
            loadVaccines(); // Reload danh sách vaccine
            const modal = bootstrap.Modal.getInstance(document.getElementById('vaccineModal'));
            modal.hide(); // Đóng modal
        })
        .catch(error => console.error('Lỗi khi lưu vaccine:', error));
}

function editVaccine(id) {
    fetch(`/vaccine/${id}`)
        .then(response => response.json())
        .then(vaccine => {
            document.getElementById('vaccineId').value = vaccine.id; // Set vaccine ID
            document.getElementById('name').value = vaccine.name;
            document.getElementById('manufacturer').value = vaccine.manufacturer;
            document.getElementById('lotNumber').value = vaccine.lotNumber;
            document.getElementById('expirationDate').value = vaccine.expirationDate;
            document.getElementById('price').value = vaccine.price;
            document.getElementById('modalTitle').textContent = 'Sửa Vaccine';
            new bootstrap.Modal(document.getElementById('vaccineModal')).show();
        })
        .catch(error => console.error('Lỗi khi tải vaccine:', error));
}

function deleteVaccine(id) {
    if (confirm('Bạn có chắc chắn muốn xóa vaccine này?')) {
        fetch(`/vaccine/${id}`, { method: 'DELETE' })
            .then(() => loadVaccines())
            .catch(error => console.error('Lỗi khi xóa vaccine:', error));
    }
}

function searchVaccine() {
    const keyword = document.getElementById('searchInput').value.toLowerCase();
    const filteredVaccines = vaccineList.filter(vaccine =>
        vaccine.name.toLowerCase().includes(keyword) ||
        vaccine.manufacturer.toLowerCase().includes(keyword) ||
        vaccine.lotNumber.toLowerCase().includes(keyword)
    );
    renderVaccines(filteredVaccines); // Hiển thị danh sách vaccine đã lọc
}

function clearSearch() {
    document.getElementById('searchInput').value = ''; // Xóa nội dung ô tìm kiếm
    renderVaccines(vaccineList); // Hiển thị lại toàn bộ danh sách vaccine
}