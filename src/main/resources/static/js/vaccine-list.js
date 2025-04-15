// Global variables
let currentPage = 0;
let totalPages = 0;
let currentView = 'grid';
let vaccines = [];
let currentVaccine = null;

// DOM Ready
document.addEventListener('DOMContentLoaded', function() {
    loadVaccines();
    initializeViewToggle();
    initializeFormValidation();
    initializeImagePreview();
});

// View Toggle
function initializeViewToggle() {
    document.querySelectorAll('.btn-toggle').forEach(button => {
        button.addEventListener('click', function() {
            const view = this.dataset.view;
            currentView = view;
            
            // Update active state
            document.querySelectorAll('.btn-toggle').forEach(btn => {
                btn.classList.remove('active');
            });
            this.classList.add('active');
            
            // Toggle view containers
            document.getElementById('vaccine-cards').style.display = view === 'grid' ? 'flex' : 'none';
            document.getElementById('vaccine-table').style.display = view === 'list' ? 'block' : 'none';
            
            // Reload current page
            loadVaccines(currentPage);
        });
    });
}

// Load vaccines with pagination
function loadVaccines(page = 0, size = 6) {
    fetch(`/vaccine/vaccine-list?page=${page}&size=${size}`)
        .then(response => response.json())
        .then(data => {
            const vaccines = data.vaccines;
            currentPage = data.currentPage;
            totalPages = data.totalPages;

            // Update view based on current mode
            if (currentView === 'grid') {
                displayGridView(vaccines);
            } else {
                displayListView(vaccines);
            }

            // Update pagination
            updatePagination(data.currentPage, data.totalPages);
        })
        .catch(error => console.error('Error:', error));
}

// Display Grid View
function displayGridView(vaccines) {
    const container = document.getElementById('vaccine-cards');
    container.innerHTML = '';

    vaccines.forEach(vaccine => {
        const card = createVaccineCard(vaccine);
        container.appendChild(card);
    });
}

// Create Vaccine Card
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
                    <strong>Hạn sử dụng:</strong> ${vaccine.expirationDate}<br>
                    <strong>Giá:</strong> ${formatCurrency(vaccine.price)}
                </p>
                <div class="card-actions">
                    <button class="btn btn-primary btn-sm" onclick="editVaccine(${vaccine.id})">
                        <i class="fas fa-edit"></i> Sửa
                    </button>
                    <button class="btn btn-danger btn-sm" onclick="deleteVaccine(${vaccine.id})">
                        <i class="fas fa-trash"></i> Xóa
                    </button>
                </div>
            </div>
        </div>
    `;
    return card;
}

// Display List View
function displayListView(vaccines) {
    const tbody = document.getElementById('vaccine-body');
    tbody.innerHTML = '';

    vaccines.forEach(vaccine => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${vaccine.name}</td>
            <td>${vaccine.manufacturer}</td>
            <td>${vaccine.lotNumber}</td>
            <td>${vaccine.expirationDate}</td>
            <td>${formatCurrency(vaccine.price)}</td>
            <td>
                <img src="data:image/jpeg;base64,${vaccine.imageBase64}" 
                     alt="${vaccine.name}" 
                     style="width: 50px; height: 50px; object-fit: cover;"
                     onerror="this.src='/img/default-vaccine.jpg'">
            </td>
            <td>
                <button class="btn btn-primary btn-sm" onclick="editVaccine(${vaccine.id})">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-danger btn-sm" onclick="deleteVaccine(${vaccine.id})">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Update Pagination
function updatePagination(currentPage, totalPages) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';

    // Previous button
    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 0 ? 'disabled' : ''}`;
    prevLi.innerHTML = `
        <a class="page-link" href="#" onclick="loadVaccines(${currentPage - 1})" aria-label="Previous">
            <span aria-hidden="true">&laquo;</span>
        </a>
    `;
    pagination.appendChild(prevLi);

    // Page numbers
    for (let i = 0; i < totalPages; i++) {
        const li = document.createElement('li');
        li.className = `page-item ${i === currentPage ? 'active' : ''}`;
        li.innerHTML = `
            <a class="page-link" href="#" onclick="loadVaccines(${i})">${i + 1}</a>
        `;
        pagination.appendChild(li);
    }

    // Next button
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}`;
    nextLi.innerHTML = `
        <a class="page-link" href="#" onclick="loadVaccines(${currentPage + 1})" aria-label="Next">
            <span aria-hidden="true">&raquo;</span>
        </a>
    `;
    pagination.appendChild(nextLi);
}

// Form Validation
function initializeFormValidation() {
    const form = document.getElementById('vaccineForm');
    form.addEventListener('submit', function(event) {
        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
        }
        form.classList.add('was-validated');
    });
}

// Image Preview
function initializeImagePreview() {
    const imageInput = document.getElementById('image');
    imageInput.addEventListener('change', function() {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const preview = document.getElementById('imagePreview');
                preview.innerHTML = `<img src="${e.target.result}" alt="Preview">`;
            };
            reader.readAsDataURL(file);
            
            // Update custom file label
            const label = document.querySelector('.custom-file-label');
            label.textContent = file.name;
        }
    });
}

// CRUD Operations
function showCreateForm() {
    currentVaccine = null;
    document.getElementById('vaccineForm').reset();
    document.getElementById('modalTitle').innerHTML = '<i class="fas fa-syringe"></i> Thêm Vaccine';
    document.getElementById('imagePreview').innerHTML = '';
    document.querySelector('.custom-file-label').textContent = 'Chọn file...';
    
    const modal = new bootstrap.Modal(document.getElementById('vaccineModal'));
    modal.show();
}

async function saveVaccine() {
    const form = document.getElementById('vaccineForm');
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return;
    }

    const formData = new FormData();
    formData.append('name', document.getElementById('name').value);
    formData.append('manufacturer', document.getElementById('manufacturer').value);
    formData.append('lotNumber', document.getElementById('lotNumber').value);
    formData.append('expirationDate', document.getElementById('expirationDate').value);
    formData.append('price', document.getElementById('price').value);
    
    const imageFile = document.getElementById('image').files[0];
    if (imageFile) {
        formData.append('image', imageFile);
    }

    try {
        const url = currentVaccine ? `/vaccine/${currentVaccine.id}` : '/vaccine';
        const method = currentVaccine ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            body: formData
        });

        if (response.ok) {
            showToast('Lưu vaccine thành công', 'success');
            bootstrap.Modal.getInstance(document.getElementById('vaccineModal')).hide();
            loadVaccines();
        } else {
            throw new Error('Lỗi khi lưu vaccine');
        }
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function editVaccine(id) {
    try {
        const response = await fetch(`/vaccine/${id}`);
        currentVaccine = await response.json();
        
        document.getElementById('vaccineId').value = currentVaccine.id;
        document.getElementById('name').value = currentVaccine.name;
        document.getElementById('manufacturer').value = currentVaccine.manufacturer;
        document.getElementById('lotNumber').value = currentVaccine.lotNumber;
        document.getElementById('expirationDate').value = currentVaccine.expirationDate;
        document.getElementById('price').value = currentVaccine.price;
        
        if (currentVaccine.imageBase64) {
            document.getElementById('imagePreview').innerHTML = `
                <img src="data:image/jpeg;base64,${currentVaccine.imageBase64}" alt="Preview">
            `;
        }
        
        document.getElementById('modalTitle').innerHTML = '<i class="fas fa-edit"></i> Sửa Vaccine';
        
        const modal = new bootstrap.Modal(document.getElementById('vaccineModal'));
        modal.show();
    } catch (error) {
        showToast('Lỗi khi tải thông tin vaccine', 'error');
    }
}

async function deleteVaccine(id) {
    if (confirm('Bạn có chắc chắn muốn xóa vaccine này?')) {
        try {
            const response = await fetch(`/vaccine/${id}`, {
                method: 'DELETE'
            });
            
            if (response.ok) {
                showToast('Xóa vaccine thành công', 'success');
                loadVaccines();
            } else {
                throw new Error('Lỗi khi xóa vaccine');
            }
        } catch (error) {
            showToast(error.message, 'error');
        }
    }
}

// Utility Functions
function formatDate(dateString) {
    return new Date(dateString).toLocaleDateString('vi-VN');
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', { 
        style: 'currency', 
        currency: 'VND' 
    }).format(amount);
}

function showToast(message, type = 'info') {
    // Implement your toast notification here
    alert(message);
}

// Search functionality
function searchVaccine() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    loadVaccines(0); // Reset to first page when searching
}

// Clear search
function clearSearch() {
    document.getElementById('searchInput').value = '';
    loadVaccines(0); // Reset to first page
}