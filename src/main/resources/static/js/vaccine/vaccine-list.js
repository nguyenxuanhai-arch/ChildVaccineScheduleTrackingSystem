let currentPage = 0;
let totalPages = 0;
let currentView = 'grid';

document.addEventListener('DOMContentLoaded', function() {
    loadVaccines();
    initializeViewToggle();
    initializeSearchEvents();
});

function initializeViewToggle() {
    document.querySelectorAll('.btn-toggle').forEach(button => {
        button.addEventListener('click', function() {
            const view = this.dataset.view;
            currentView = view;

            document.querySelectorAll('.btn-toggle').forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');

            document.getElementById('vaccine-cards').style.display = view === 'grid' ? 'flex' : 'none';
            document.getElementById('vaccine-table').style.display = view === 'list' ? 'block' : 'none';

            loadVaccines(currentPage);
        });
    });
}

function initializeSearchEvents() {
    const input = document.getElementById('searchInput');
    const button = document.getElementById('searchButton');
    if (!input || !button) return;

    // Bấm nút tìm kiếm
    button.addEventListener('click', function() {
        searchVaccine();
    });

    // Gõ đến đâu search đến đó (real-time)
    input.addEventListener('input', function () {
        searchVaccine();
    });
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
        const card = document.createElement('div');
        card.className = 'card';
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
                </div>
            </div>
        `;
        container.appendChild(card);
    });
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

function showLoading() {
    const loader = document.getElementById('loading-indicator');
    if (loader) loader.style.display = 'block';
}

function hideLoading() {
    const loader = document.getElementById('loading-indicator');
    if (loader) loader.style.display = 'none';
}
