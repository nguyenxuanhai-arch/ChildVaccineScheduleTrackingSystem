
document.addEventListener('DOMContentLoaded', () => {
    loadChildren();
    loadVaccines();
    setupPackageTab();
    
    // Set min date for appointment to today
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('vaccineAppointmentDate').min = today;
    document.getElementById('packageAppointmentDate').min = today;
});

// Hàm format giới tính
function formatGender(gender) {
    if (!gender) return "Không rõ";
    const normalized = gender.toLowerCase();
    if (normalized === 'male') return 'Nam';
    if (normalized === 'female') return 'Nữ';
    return gender;
}

// Format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

// Tải danh sách bé của phụ huynh
function loadChildren() {
    const token = localStorage.getItem('token');

    axios.get('/auth/profile/children/list', {
        headers: {
            Authorization: `Bearer ${token}`
        }
    })
    .then(response => {
        console.log('Dữ liệu trẻ em:', response.data);
        const vaccineChildSelect = document.getElementById('vaccineChildId');
        const packageChildSelect = document.getElementById('packageChildId');
        
        vaccineChildSelect.innerHTML = '<option value="">Chọn bé</option>';
        packageChildSelect.innerHTML = '<option value="">Chọn bé</option>';
        
        if (response.data && response.data.length > 0) {
            response.data.forEach(child => {
                // Format DOB to match profile page display
                const dob = new Date(child.dob);
                const formattedDob = dob.toLocaleDateString('vi-VN');
                
                // Format gender consistently
                const genderText = child.gender === 'MALE' ? 'Nam' : (child.gender === 'FEMALE' ? 'Nữ' : 'Không rõ');
                
                // Calculate child's age in months for filtering
                const birthDate = new Date(child.dob);
                const today = new Date();
                const ageInMonths = (today.getFullYear() - birthDate.getFullYear()) * 12 + 
                                    today.getMonth() - birthDate.getMonth();
                
                const vaccineOption = document.createElement('option');
                vaccineOption.value = child.id;
                vaccineOption.textContent = `${child.name} (${formattedDob}, ${genderText})`;
                vaccineOption.dataset.ageInMonths = ageInMonths;
                vaccineChildSelect.appendChild(vaccineOption);
                
                const packageOption = document.createElement('option');
                packageOption.value = child.id;
                packageOption.textContent = `${child.name} (${formattedDob}, ${genderText})`;
                packageOption.dataset.ageInMonths = ageInMonths;
                packageChildSelect.appendChild(packageOption);
            });
        } else {
            document.getElementById('vaccineMessage').innerHTML = 
                '<div class="alert alert-warning">Bạn chưa có hồ sơ trẻ em. Vui lòng thêm hồ sơ trẻ trước.</div>';
            document.getElementById('packageMessage').innerHTML = 
                '<div class="alert alert-warning">Bạn chưa có hồ sơ trẻ em. Vui lòng thêm hồ sơ trẻ trước.</div>';
        }
    })
    .catch(error => {
        console.error('Lỗi khi tải danh sách bé:', error);
        document.getElementById('vaccineMessage').innerHTML = 
            '<div class="alert alert-danger">Không thể tải danh sách bé. Vui lòng đăng nhập lại.</div>';
        document.getElementById('packageMessage').innerHTML = 
            '<div class="alert alert-danger">Không thể tải danh sách bé. Vui lòng đăng nhập lại.</div>';
    });
}

// Tải danh sách vắc xin
function loadVaccines() {
    axios.get('/vaccine/vaccine-list')
        .then(response => {
            const vaccineSelect = document.getElementById('vaccineId');
            vaccineSelect.innerHTML = '';

            // Add a default option
            const defaultOption = document.createElement('option');
            defaultOption.value = '';
            defaultOption.text = '-- Chọn vắc xin --';
            vaccineSelect.appendChild(defaultOption);

            if (response.data && response.data.vaccines && response.data.vaccines.length > 0) {
            response.data.vaccines.forEach(vaccine => {
                const option = document.createElement('option');
                option.value = vaccine.id;
                    option.text = `${vaccine.name} - ${formatCurrency(vaccine.price)}`;
                vaccineSelect.appendChild(option);
            });
            } else {
                document.getElementById('vaccineMessage').innerHTML += 
                    '<div class="alert alert-warning">Không có vắc xin nào trong hệ thống.</div>';
            }
        })
        .catch(error => {
            console.error('Lỗi khi load danh sách vắc xin:', error);
            document.getElementById('vaccineMessage').innerHTML += 
                '<div class="alert alert-danger">Không thể tải danh sách vắc xin.</div>';
        });
}

// Setup package tab
function setupPackageTab() {
    document.getElementById('package-tab').addEventListener('click', () => {
        loadPackages();
    });
}

// Load packages
function loadPackages(type = 'all') {
    const packagesContainer = document.getElementById('packagesContainer');
    packagesContainer.innerHTML = `
        <div class="col-12 text-center py-4">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-2">Đang tải dữ liệu gói vắc xin...</p>
        </div>
    `;

    let url = '/api/packages';
    if (type !== 'all') {
        url = `/api/packages/type/${type}`;
    }

    axios.get(url)
        .then(response => {
            const packages = response.data;
            
            if (packages && packages.length > 0) {
                displayPackages(packages);
            } else {
                packagesContainer.innerHTML = `
                    <div class="col-12 text-center py-4">
                        <i class="bi bi-info-circle text-info fs-1"></i>
                        <p class="mt-2">Không có gói vắc xin nào phù hợp.</p>
                    </div>
                `;
            }
        })
        .catch(error => {
            console.error('Lỗi khi tải gói vắc xin:', error);
            packagesContainer.innerHTML = `
                <div class="col-12 text-center py-4">
                    <i class="bi bi-exclamation-triangle text-danger fs-1"></i>
                    <p class="mt-2">Không thể tải dữ liệu gói vắc xin. Vui lòng thử lại sau.</p>
                </div>
            `;
        });
}

// Display packages
function displayPackages(packages) {
    const packagesContainer = document.getElementById('packagesContainer');
    packagesContainer.innerHTML = '';
    
    // Get child age in months if a child is selected
    let childAgeInMonths = null;
    const packageChildSelect = document.getElementById('packageChildId');
    if (packageChildSelect.selectedIndex > 0) {
        const selectedOption = packageChildSelect.options[packageChildSelect.selectedIndex];
        childAgeInMonths = parseInt(selectedOption.dataset.ageInMonths);
    }

    packages.forEach(pkg => {
        // Determine if package is suitable for the selected child's age
        let isSuitableForAge = true;
        let ageWarning = '';
        
        if (childAgeInMonths !== null) {
            isSuitableForAge = childAgeInMonths >= pkg.ageRangeStart && childAgeInMonths <= pkg.ageRangeEnd;
            if (!isSuitableForAge) {
                ageWarning = `<div class="text-danger small mt-1"><i class="bi bi-exclamation-triangle"></i> Không phù hợp với độ tuổi của bé</div>`;
            }
        }

        // Create a badge for package type
        let typeBadge = '';
        if (pkg.type === 'INDIVIDUAL') {
            typeBadge = '<span class="badge bg-primary me-2">Tiêm lẻ</span>';
        } else if (pkg.type === 'PACKAGE') {
            typeBadge = '<span class="badge bg-success me-2">Trọn gói</span>';
        } else if (pkg.type === 'CUSTOM') {
            typeBadge = '<span class="badge bg-info me-2">Cá thể hóa</span>';
        }

        const packageCard = document.createElement('div');
        packageCard.className = 'col-md-6 mb-3';
        packageCard.innerHTML = `
            <div class="card package-card h-100" data-package-id="${pkg.id}" data-age-start="${pkg.ageRangeStart}" data-age-end="${pkg.ageRangeEnd}">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <div>
                            ${typeBadge}
                            <h5 class="package-title mb-0">${pkg.name}</h5>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="packageSelection" id="package${pkg.id}" value="${pkg.id}" ${!isSuitableForAge ? 'disabled' : ''}>
                        </div>
                    </div>
                    <p class="package-desc mb-2">${pkg.description || 'Không có mô tả'}</p>
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <span class="package-age">Độ tuổi: ${pkg.ageRangeStart}-${pkg.ageRangeEnd} tháng</span>
                        <span class="package-price">${formatCurrency(pkg.price)}</span>
                    </div>
                    <div class="package-vaccines small text-muted mt-1">
                        <strong>Vắc xin (${pkg.vaccines ? pkg.vaccines.length : 0}):</strong> 
                        ${pkg.vaccines ? pkg.vaccines.map(v => v.name).join(', ') : 'Không có'}
                    </div>
                    ${ageWarning}
                </div>
            </div>
        `;
        
        packagesContainer.appendChild(packageCard);
    });

    // Add event listeners to package selection
    document.querySelectorAll('input[name="packageSelection"]').forEach(radio => {
        radio.addEventListener('change', function() {
            // Update hidden input with selected package id
            document.getElementById('selectedPackageId').value = this.value;
            
            // Enable the book button
            document.getElementById('btnBookPackage').disabled = false;
            document.getElementById('packageSelectionWarning').style.display = 'none';
            
            // Add 'selected' class to the selected package card
            document.querySelectorAll('.package-card').forEach(card => {
                card.classList.remove('selected');
            });
            this.closest('.package-card').classList.add('selected');
        });
    });

    // Filter packages based on child's age when child selection changes
    document.getElementById('packageChildId').addEventListener('change', function() {
        if (this.selectedIndex > 0) {
            const selectedOption = this.options[this.selectedIndex];
            const childAgeInMonths = parseInt(selectedOption.dataset.ageInMonths);
            
            document.querySelectorAll('.package-card').forEach(card => {
                const ageStart = parseInt(card.dataset.ageStart);
                const ageEnd = parseInt(card.dataset.ageEnd);
                const isSuitable = childAgeInMonths >= ageStart && childAgeInMonths <= ageEnd;
                
                const radio = card.querySelector('input[type="radio"]');
                radio.disabled = !isSuitable;
                
                if (!isSuitable) {
                    radio.checked = false;
                    card.classList.remove('selected');
                    
                    // Add age warning if not already present
                    const warningDiv = card.querySelector('.text-danger.small');
                    if (!warningDiv) {
                        const vaccinesDiv = card.querySelector('.package-vaccines');
                        const warning = document.createElement('div');
                        warning.className = 'text-danger small mt-1';
                        warning.innerHTML = '<i class="bi bi-exclamation-triangle"></i> Không phù hợp với độ tuổi của bé';
                        vaccinesDiv.after(warning);
                    }
                } else {
                    // Remove age warning if present
                    const warningDiv = card.querySelector('.text-danger.small');
                    if (warningDiv) {
                        warningDiv.remove();
                    }
                }
            });
            
            // Update book button status
            const anyChecked = document.querySelector('input[name="packageSelection"]:checked');
            document.getElementById('btnBookPackage').disabled = !anyChecked;
            document.getElementById('packageSelectionWarning').style.display = anyChecked ? 'none' : 'inline';
        }
    });
}

// Filter packages by type
function filterPackages(type) {
    // Update active button
    document.querySelectorAll('#btnAllPackages, #btnStandardPackages, #btnCustomPackages').forEach(btn => {
        btn.classList.remove('active');
    });
    
    if (type === 'all') {
        document.getElementById('btnAllPackages').classList.add('active');
    } else if (type === 'PACKAGE') {
        document.getElementById('btnStandardPackages').classList.add('active');
    } else if (type === 'CUSTOM') {
        document.getElementById('btnCustomPackages').classList.add('active');
    }
    
    loadPackages(type);
}

// Thêm bé mới
document.getElementById('addChildForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const data = {
        name: document.getElementById('childName').value,
        dob: document.getElementById('childDob').value,
        gender: document.getElementById('childGender').value === 'male' ? 'MALE' : 'FEMALE' // Convert to match backend format
    };

    const token = localStorage.getItem('token');

    axios.post('/auth/profile/children/add', data, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    })
    .then(response => {
        document.getElementById('addChildMessage').innerHTML = 
            `<div class="alert alert-success">✅ Thêm bé thành công!</div>`;
        document.getElementById('addChildForm').reset();
        // Load lại danh sách bé sau khi thêm thành công
        loadChildren();
    })
    .catch(error => {
        document.getElementById('addChildMessage').innerHTML = 
            `<div class="alert alert-danger">❌ Thêm bé thất bại! ${error.response?.data?.message || error.response?.data || 'Vui lòng thử lại'}</div>`;
        console.error(error);
    });
});

// Đặt lịch tiêm vaccine đơn lẻ
document.getElementById('vaccineAppointmentForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const data = {
        childId: document.getElementById('vaccineChildId').value,
        vaccineId: document.getElementById('vaccineId').value,
        appointmentDate: document.getElementById('vaccineAppointmentDate').value,
        appointmentTime: document.getElementById('vaccineAppointmentTime').value,
        notes: document.getElementById('vaccineNotes').value,
        type: 'VACCINE'
    };

    const token = localStorage.getItem('token');

    axios.post('/appointments/book', data, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    })
        .then(response => {
            document.getElementById('vaccineMessage').innerHTML = 
                '<div class="alert alert-success">✅ Đặt lịch tiêm vắc xin thành công!</div>';
            // Redirect to home page after 1.5 seconds
            setTimeout(() => {
                window.location.href = '/';
            }, 1500);
        })
        .catch(error => {
            document.getElementById('vaccineMessage').innerHTML = 
                `<div class="alert alert-danger">❌ Đặt lịch thất bại! ${error.response?.data || ''}</div>`;
            console.error(error);
        });
});

// Đặt lịch tiêm gói vắc xin
document.getElementById('packageAppointmentForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const packageId = document.getElementById('selectedPackageId').value;
    if (!packageId) {
        document.getElementById('packageMessage').innerHTML = 
            '<div class="alert alert-warning">Vui lòng chọn gói vắc xin trước khi đặt lịch.</div>';
        return;
    }

    const data = {
        childId: document.getElementById('packageChildId').value,
        packageId: packageId,
        appointmentDate: document.getElementById('packageAppointmentDate').value,
        appointmentTime: document.getElementById('packageAppointmentTime').value,
        notes: document.getElementById('packageNotes').value,
        type: 'PACKAGE'
    };

    const token = localStorage.getItem('token');

    axios.post('/appointments/book-package', data, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    })
    .then(response => {
        document.getElementById('packageMessage').innerHTML = 
            '<div class="alert alert-success">✅ Đặt lịch tiêm gói vắc xin thành công!</div>';
        
        // Redirect to home page after 1.5 seconds
        setTimeout(() => {
            window.location.href = '/';
        }, 1500);
        
        // Rest of the reset code
        document.getElementById('packageAppointmentForm').reset();
        document.getElementById('selectedPackageId').value = '';
        document.getElementById('btnBookPackage').disabled = true;
        document.getElementById('packageSelectionWarning').style.display = 'inline';
        
        // Remove 'selected' class from all package cards
        document.querySelectorAll('.package-card').forEach(card => {
            card.classList.remove('selected');
        });
        
        // Uncheck all radio buttons
        document.querySelectorAll('input[name="packageSelection"]').forEach(radio => {
            radio.checked = false;
        });
    })
    .catch(error => {
        document.getElementById('packageMessage').innerHTML = 
            `<div class="alert alert-danger">❌ Đặt lịch thất bại! ${error.response?.data || ''}</div>`;
        console.error(error);
    });
});

// Cập nhật danh sách vaccine dựa trên bé được chọn
function updateVaccineList() {
    const vaccineChildSelect = document.getElementById('vaccineChildId');
    const vaccineSelect = document.getElementById('vaccineId');
    
    // Reset vaccine select
    vaccineSelect.innerHTML = '<option value="">Chọn vaccine</option>';
    
    // If a child is selected
    if (vaccineChildSelect.selectedIndex > 0) {
        const selectedOption = vaccineChildSelect.options[vaccineChildSelect.selectedIndex];
        const childAgeInMonths = parseInt(selectedOption.dataset.ageInMonths);
        
        // Filter and add vaccines based on child's age
        // ... existing code ...
    }
}
