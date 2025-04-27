// Lấy dữ liệu từ backend được gắn vào các thuộc tính data-* của một div ẩn
const dashboardData = {
    totalUsers: parseInt(document.querySelector('#dashboardData').dataset.totalUsers),
    totalVaccines: parseInt(document.querySelector('#dashboardData').dataset.totalVaccines),
    totalAppointments: parseInt(document.querySelector('#dashboardData').dataset.totalAppointments),
    totalRevenue: parseFloat(document.querySelector('#dashboardData').dataset.totalRevenue),
    todayAppointments: parseInt(document.querySelector('#dashboardData').dataset.todayAppointments),
    pendingAppointments: parseInt(document.querySelector('#dashboardData').dataset.pendingAppointments),
    pendingPayments: parseInt(document.querySelector('#dashboardData').dataset.pendingPayments)
};

// Biểu đồ đường thể hiện xu hướng đặt lịch hẹn
const appointmentTrendOptions = {
    series: [{
        name: 'Appointments',
        data: [30, 40, 35, 50, 49, 60, 70, 91, 125]
    }],
    chart: {
        height: 350,
        type: 'line',
        zoom: {
            enabled: false
        }
    },
    dataLabels: {
        enabled: false
    },
    stroke: {
        curve: 'straight'
    },
    grid: {
        row: {
            colors: ['#f3f3f3', 'transparent'],
            opacity: 0.5
        },
    },
    xaxis: {
        categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep'],
    }
};

const appointmentTrendChart = new ApexCharts(document.querySelector("#appointmentTrendChart"), appointmentTrendOptions);
appointmentTrendChart.render();

// Biểu đồ tròn thể hiện phân bố trạng thái thanh toán
const paymentStatusOptions = {
    series: [dashboardData.totalPayments - dashboardData.pendingPayments, dashboardData.pendingPayments],
    chart: {
        type: 'donut',
        height: 350
    },
    labels: ['Completed', 'Pending'],
    colors: ['#198754', '#ffc107'],
    responsive: [{
        breakpoint: 480,
        options: {
            chart: {
                width: 200
            },
            legend: {
                position: 'bottom'
            }
        }
    }]
};

const paymentStatusChart = new ApexCharts(document.querySelector("#paymentStatusChart"), paymentStatusOptions);
paymentStatusChart.render();

// Biểu đồ cột thể hiện số lượng lịch hẹn theo trạng thái
const appointmentStatusOptions = {
    series: [{
        name: 'Appointments',
        data: [dashboardData.todayAppointments, dashboardData.pendingAppointments, 
               dashboardData.totalAppointments - dashboardData.pendingAppointments - dashboardData.todayAppointments]
    }],
    chart: {
        height: 350,
        type: 'bar',
    },
    plotOptions: {
        bar: {
            borderRadius: 4,
            horizontal: true,
        }
    },
    colors: ['#dc3545', '#ffc107', '#198754'],
    dataLabels: {
        enabled: false
    },
    xaxis: {
        categories: ['Today', 'Pending', 'Completed'],
    }
};

const appointmentStatusChart = new ApexCharts(document.querySelector("#appointmentStatusChart"), appointmentStatusOptions);
appointmentStatusChart.render();

// Revenue trend chart
const revenueTrendOptions = {
    series: [{
        name: 'Revenue',
        data: [3000000, 4500000, 5200000, 4800000, 6100000, 7200000]
    }],
    chart: {
        height: 350,
        type: 'area',
    },
    dataLabels: {
        enabled: false
    },
    stroke: {
        curve: 'smooth'
    },
    xaxis: {
        categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    },
    yaxis: {
        labels: {
            formatter: function (value) {
                return new Intl.NumberFormat('vi-VN', {
                    style: 'currency',
                    currency: 'VND',
                    minimumFractionDigits: 0,
                    maximumFractionDigits: 0
                }).format(value);
            }
        }
    },
    tooltip: {
        y: {
            formatter: function (value) {
                return new Intl.NumberFormat('vi-VN', {
                    style: 'currency',
                    currency: 'VND',
                    minimumFractionDigits: 0,
                    maximumFractionDigits: 0
                }).format(value);
            }
        }
    }
};

const revenueTrendChart = new ApexCharts(document.querySelector("#revenueTrendChart"), revenueTrendOptions);
revenueTrendChart.render();

// Handle date range selector
document.getElementById('dateRange').addEventListener('change', function(e) {
    const customDateRange = document.getElementById('customDateRange');
    if (e.target.value === 'custom') {
        customDateRange.classList.remove('d-none');
    } else {
        customDateRange.classList.add('d-none');
    }
});

// Handle report form submission
document.getElementById('reportForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const reportType = document.getElementById('reportType').value;
    const dateRange = document.getElementById('dateRange').value;
    const format = document.getElementById('format').value;
    
    let dateParams = '';
    if (dateRange === 'custom') {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        if (!startDate || !endDate) {
            alert('Please select both start and end dates for custom range');
            return;
        }
        dateParams = `&startDate=${startDate}&endDate=${endDate}`;
    }
    
    // Make API call to generate report
    window.location.href = `/api/admin/reports/generate?type=${reportType}&dateRange=${dateRange}&format=${format}${dateParams}`;
});
