// Cấu hình OverlayScrollbars cho sidebar
const SELECTOR_SIDEBAR_WRAPPER = '.sidebar-wrapper';
const Default = {
  scrollbarTheme: 'os-theme-light',
  scrollbarAutoHide: 'leave',
  scrollbarClickScroll: true,
};
document.addEventListener('DOMContentLoaded', function () {
  try {
    const sidebarWrapper = document.querySelector(SELECTOR_SIDEBAR_WRAPPER);
    if (sidebarWrapper && typeof OverlayScrollbarsGlobal?.OverlayScrollbars !== 'undefined') {
      OverlayScrollbarsGlobal.OverlayScrollbars(sidebarWrapper, {
        scrollbars: {
          theme: Default.scrollbarTheme,
          autoHide: Default.scrollbarAutoHide,
          clickScroll: Default.scrollbarClickScroll,
        },
      });
    }

    const connectedSortables = document.querySelectorAll('.connectedSortable');
    connectedSortables.forEach((connectedSortable) => {
      let sortable = new Sortable(connectedSortable, {
        group: 'shared',
        handle: '.card-header',
      });
    });

    const cardHeaders = document.querySelectorAll('.connectedSortable .card-header');
    cardHeaders.forEach((cardHeader) => {
      cardHeader.style.cursor = 'move';
    });

    const revenueChartElement = document.querySelector('#revenue-chart');
    if (revenueChartElement) {
      const sales_chart = new ApexCharts(revenueChartElement, sales_chart_options);
      sales_chart.render();
    }
  } catch (error) {
    console.error('Lỗi khi thực thi DOMContentLoaded:', error);
  }
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

// Hàm đăng xuất
function logout() {
  fetch('/auth/logout', {
    method: 'POST',
    credentials: 'include',
  }).finally(() => {
    window.location.href = '/admin/login';
  });
}

// Gắn sự kiện cho nút đăng xuất
const signOutButton = document.getElementById('signOutBtn');
if (signOutButton) {
  signOutButton.addEventListener('click', function (e) {
    e.preventDefault();
    logout();
  });
}


