let currentPage = 0;
let totalPages = 0;
let currentView = 'grid';

document.addEventListener('DOMContentLoaded', function() {
    loadVaccines();
    initializeViewToggle();
    initializeSearchEvents();
    initializeBookingButtonEvent();
});

// View Toggle
function initializeViewToggle() {
    document.querySelectorAll('.btn-toggle').forEach(button => {
        button.addEventListener('click', function() {
            const view = this.dataset.view;
            currentView = view;

            document.querySelectorAll('.btn-toggle').forEach(btn => {
                btn.classList.remove('active');
            });
            this.classList.add('active');

            document.getElementById('vaccine-cards').style.display = view === 'grid' ? 'flex' : 'none';
            document.getElementById('vaccine-table').style.display = view === 'list' ? 'block' : 'none';

            loadVaccines(currentPage);
        });
    });
}

// Booking button (event delegation)
function initializeBookingButtonEvent() {
    document.addEventListener('click', function(e) {
        const btn = e.target.closest('.booking-btn');
        if (btn) {
            const id = btn.dataset.id;
            const name = btn.dataset.name;
            const price = btn.dataset.price;
            showBookingModal(id, name, price);
        }
    });
}

// Search functionality
function initializeSearchEvents() {
    const input = document.getElementById('searchInput');
    if (!input) return;

    input.addEventListener('keyup', function(e) {
        if (e.key === 'Enter') {
            searchVaccine();
        }
    });

    // Optional: Real-time search (debounce)
    // let timeout;
    // input.addEventListener('input', () => {
    //     clearTimeout(timeout);
    //     timeout = setTimeout(() => searchVaccine(), 300);
    // });
}

function searchVaccine() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();

    fetch(`/vaccine/vaccine-list?page=0&size=6&search=${encodeURIComponent(searchTerm)}`)
        .then(response => response.json())
        .then(data => {
            const vaccines = data.vaccines;
            currentPage = data.currentPage;
            totalPages = data.totalPages;

            if (currentView === 'grid') {
                displayGridView(vaccines);
            } else {
                displayListView(vaccines);
            }

            updatePagination(data.currentPage, data.totalPages);
        })
        .catch(error => console.error('Error:', error));
}

function clearSearch() {
    document.getElementById('searchInput').value = '';
    loadVaccines(0);
}

// Load vaccines
function loadVaccines(page = 0, size = 6) {
    showLoading();

    fetch(`/vaccine/vaccine-list?page=${page}&size=${size}`)
        .then(response => response.json())
        .then(data => {
            const vaccines = data.vaccines;
            currentPage = data.currentPage;
            totalPages = data.totalPages;

            if (currentView === 'grid') {
                displayGridView(vaccines);
            } else {
                displayListView(vaccines);
            }

            updatePagination(data.currentPage, data.totalPages);
        })
        .catch(error => console.error('Error:', error))
        .finally(() => hideLoading());
}

function displayGridView(vaccines) {
    const container = document.getElementById('vaccine-cards');
    container.innerHTML = '';

    vaccines.forEach(vaccine => {
        const card = createVaccineCard(vaccine);
        container.appendChild(card);
    });
}

function createVaccineCard(vaccine) {
    const card = document.createElement('div');
    card.className = 'col-md-4 mb-4';
    card.innerHTML = `
        <div class="card h-100">
            <div class="card-img-container">
                <img src="data:image/jpeg;base64,${vaccine.imageBase64}" 
                     class="card-img-top" 
                     alt="${vaccine.name}"
                     onerror="this.src='/img/default-vaccine.jpg'">
            </div>
            <div class="card-body">
                <h5 class="card-title">${vaccine.name}</h5>
                <p class="card-text">
                    <strong>Nhà sản xuất:</strong> ${vaccine.manufacturer}<br>
                    <strong>Số lô:</strong> ${vaccine.lotNumber}<br>
                    <strong>Hạn sử dụng:</strong> ${formatDate(vaccine.expirationDate)}<br>
                    <strong>Giá:</strong> ${formatCurrency(vaccine.price)}
                </p>
                <div class="card-actions">
                    <button class="btn btn-success booking-btn"
                            data-id="${vaccine.id}"
                            data-name="${vaccine.name}"
                            data-price="${vaccine.price}">
                        <i class="fas fa-calendar-check"></i> Đặt lịch tiêm
                    </button>
                </div>
            </div>
        </div>
    `;
    return card;
}

function displayListView(vaccines) {
    const tbody = document.getElementById('vaccine-body');
    tbody.innerHTML = '';

    vaccines.forEach(vaccine => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${vaccine.name}</td>
            <td>${vaccine.manufacturer}</td>
            <td>${vaccine.lotNumber}</td>
            <td>${formatDate(vaccine.expirationDate)}</td>
            <td>${formatCurrency(vaccine.price)}</td>
            <td>
                <img src="data:image/jpeg;base64,${vaccine.imageBase64}" 
                     alt="${vaccine.name}" 
                     style="width: 50px; height: 50px; object-fit: cover;"
                     onerror="this.src='/img/default-vaccine.jpg'">
            </td>
            <td>
                <button class="btn btn-success booking-btn"
                        data-id="${vaccine.id}"
                        data-name="${vaccine.name}"
                        data-price="${vaccine.price}">
                    <i class="fas fa-calendar-check"></i> Đặt lịch
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function updatePagination(currentPage, totalPages) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';

    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 0 ? 'disabled' : ''}`;
    prevLi.innerHTML = `
        <a class="page-link" href="#" onclick="loadVaccines(${currentPage - 1})" aria-label="Previous">
            <span aria-hidden="true">&laquo;</span>
        </a>
    `;
    pagination.appendChild(prevLi);

    for (let i = 0; i < totalPages; i++) {
        const li = document.createElement('li');
        li.className = `page-item ${i === currentPage ? 'active' : ''}`;
        li.innerHTML = `
            <a class="page-link" href="#" onclick="loadVaccines(${i})">${i + 1}</a>
        `;
        pagination.appendChild(li);
    }

    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}`;
    nextLi.innerHTML = `
        <a class="page-link" href="#" onclick="loadVaccines(${currentPage + 1})" aria-label="Next">
            <span aria-hidden="true">&raquo;</span>
        </a>
    `;
    pagination.appendChild(nextLi);
}

function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('vi-VN');
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// Optional: Loading indicator
function showLoading() {
    const loader = document.getElementById('loading-indicator');
    if (loader) loader.style.display = 'block';
}
function hideLoading() {
    const loader = document.getElementById('loading-indicator');
    if (loader) loader.style.display = 'none';
}
let isEdit = false; // Flag kiểm tra sửa hay tạo mới

function showBookingModal(id, name, price, appointmentId = null) {
    document.getElementById('vaccineNameTitle').textContent = name;
    document.getElementById('vaccineId').value = id;
    
    // Nếu là sửa, điền thông tin hiện tại vào modal
    if (appointmentId) {
        isEdit = true;
        document.getElementById('appointmentId').value = appointmentId; // ID của cuộc hẹn
        // Fetch thông tin cuộc hẹn từ backend (lấy trước khi sửa) 
        fetch(`/appointments/${appointmentId}`)
            .then(response => response.json())
            .then(data => {
                document.getElementById('appointmentDate').value = data.appointmentDate;
                document.getElementById('appointmentTime').value = data.appointmentTime;
                document.getElementById('notes').value = data.notes;
            })
            .catch(error => console.error('Lỗi khi lấy thông tin cuộc hẹn:', error));
    } else {
        isEdit = false;
        document.getElementById('appointmentForm').reset(); // Reset form nếu tạo mới
    }

    const modal = new bootstrap.Modal(document.getElementById('bookingModal'));
    modal.show();
}

document.getElementById('bookingForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const vaccineId = document.getElementById('vaccineId').value;
    const childId = document.getElementById('childId').value;
    const appointmentDate = document.getElementById('appointmentDate').value;
    const appointmentTime = document.getElementById('appointmentTime').value;
    const notes = document.getElementById('notes').value;
    const appointmentId = document.getElementById('appointmentId').value;

    const appointmentData = {
        vaccine: { id: vaccineId },
        child: { id: childId },
        appointmentDate: appointmentDate,
        appointmentTime: appointmentTime,
        notes: notes
    };

    const url = isEdit ? `/appointments/${appointmentId}` : '/appointments/book';
    const method = isEdit ? 'PUT' : 'POST';

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(appointmentData)
    })
    .then(response => {
        if (!response.ok) throw new Error('Lỗi khi đặt hoặc sửa lịch');
        return response.json();
    })
    .then(data => {
        alert(isEdit ? "Cập nhật lịch tiêm thành công!" : "Đặt lịch thành công!");
        bootstrap.Modal.getInstance(document.getElementById('bookingModal')).hide();
    })
    .catch(error => {
        console.error(error);
        alert("Có lỗi xảy ra khi đặt hoặc sửa lịch. Vui lòng thử lại.");
    });
});