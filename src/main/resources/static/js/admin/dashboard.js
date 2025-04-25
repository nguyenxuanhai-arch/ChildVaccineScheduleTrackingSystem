// Cấu hình OverlayScrollbars cho sidebar
const SELECTOR_SIDEBAR_WRAPPER = '.sidebar-wrapper';
const Default = {
  scrollbarTheme: 'os-theme-light',
  scrollbarAutoHide: 'leave',
  scrollbarClickScroll: true,
};

document.addEventListener('DOMContentLoaded', function () {
  const sidebarWrapper = document.querySelector(SELECTOR_SIDEBAR_WRAPPER);
  if (
    sidebarWrapper &&
    typeof OverlayScrollbarsGlobal?.OverlayScrollbars !== 'undefined'
  ) {
    OverlayScrollbarsGlobal.OverlayScrollbars(sidebarWrapper, {
      scrollbars: {
        theme: Default.scrollbarTheme,
        autoHide: Default.scrollbarAutoHide,
        clickScroll: Default.scrollbarClickScroll,
      },
    });
  }
});

// Cấu hình SortableJS cho các phần tử có class "connectedSortable"
const connectedSortables = document.querySelectorAll('.connectedSortable');
connectedSortables.forEach((connectedSortable) => {
  let sortable = new Sortable(connectedSortable, {
    group: 'shared',
    handle: '.card-header',
  });
});

// Đặt con trỏ chuột thành "move" cho các phần tử có class "card-header"
const cardHeaders = document.querySelectorAll('.connectedSortable .card-header');
cardHeaders.forEach((cardHeader) => {
  cardHeader.style.cursor = 'move';
});

// Cấu hình biểu đồ doanh thu
const sales_chart_options = {
  series: [
    {
      name: 'Digital Goods',
      data: [28, 48, 40, 19, 86, 27, 90],
    },
    {
      name: 'Electronics',
      data: [65, 59, 80, 81, 56, 55, 40],
    },
  ],
  chart: {
    height: 300,
    type: 'area',
    toolbar: {
      show: false,
    },
  },
  legend: {
    show: false,
  },
  colors: ['#0d6efd', '#20c997'],
  dataLabels: {
    enabled: false,
  },
  stroke: {
    curve: 'smooth',
  },
  xaxis: {
    type: 'datetime',
    categories: [
      '2023-01-01',
      '2023-02-01',
      '2023-03-01',
      '2023-04-01',
      '2023-05-01',
      '2023-06-01',
      '2023-07-01',
    ],
  },
  tooltip: {
    x: {
      format: 'MMMM yyyy',
    },
  },
};

// Khởi tạo biểu đồ doanh thu
const revenueChartElement = document.querySelector('#revenue-chart');
if (revenueChartElement) {
  const sales_chart = new ApexCharts(revenueChartElement, sales_chart_options);
  sales_chart.render();
}

// Hàm đăng xuất
function logout() {
  fetch('/auth/logout', {
    method: 'POST',
    credentials: 'include', // Gửi cookie cùng request
  }).finally(() => {
    window.location.href = '/admin/login';
  });
}

// Thêm sự kiện click vào nút Sign out
const signOutButton = document.getElementById('signOutBtn');
if (signOutButton) {
  signOutButton.addEventListener('click', function (e) {
    e.preventDefault(); // Ngăn chặn hành động mặc định của thẻ <a>
    logout(); // Gọi function logout
  });
}

// Chạy khi trang được load
document.addEventListener('DOMContentLoaded', () => {
  // Không cần kiểm tra auth thủ công nữa, Spring Security sẽ tự xử lý
});
