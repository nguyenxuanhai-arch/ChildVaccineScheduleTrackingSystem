// Cấu hình OverlayScrollbars cho sidebar
const SELECTOR_SIDEBAR_WRAPPER = ".sidebar-wrapper";
document.addEventListener("DOMContentLoaded", function () {
  const sidebarWrapper = document.querySelector(SELECTOR_SIDEBAR_WRAPPER);
  if (
    sidebarWrapper &&
    typeof OverlayScrollbarsGlobal?.OverlayScrollbars !== "undefined"
  ) {
    OverlayScrollbarsGlobal.OverlayScrollbars(sidebarWrapper, {
      scrollbars: {
        theme: "os-theme-light", // Hoặc 'os-theme-dark'
        autoHide: "leave",
        clickScroll: true,
      },
    });
  }
});

// Hàm đăng xuất
function logout() {
  const form = document.createElement("form");
  form.method = "POST";
  form.action = "/logout"; // Đường dẫn đăng xuất
  // Thêm CSRF token nếu cần
  document.body.appendChild(form);
  form.submit();
}

// Gắn sự kiện cho nút đăng xuất
const signOutButton = document.getElementById("signOutBtn");
if (signOutButton) {
  signOutButton.addEventListener("click", function (e) {
    e.preventDefault();
    logout();
  });
}

// Đánh dấu trạng thái "active" cho sidebar
document.addEventListener("DOMContentLoaded", () => {
  const currentPath = window.location.pathname;
  const sidebarLinks = document.querySelectorAll(".sidebar-menu .nav-link");

  sidebarLinks.forEach((link) => {
    const linkPath = link.getAttribute("href");
    // Xóa trạng thái "active" khỏi tất cả các liên kết
    link.classList.remove("active");
    if (link.closest(".nav-item")) {
      link.closest(".nav-item").classList.remove("menu-open"); // Đóng các menu con
    }

    // Đánh dấu trạng thái "active" cho liên kết khớp với đường dẫn hiện tại
    if (linkPath && currentPath.startsWith(linkPath) && linkPath !== "#") {
      link.classList.add("active");
      // Mở menu con nếu liên kết thuộc menu con
      const parentTreeview = link.closest(".nav-item.has-treeview");
      if (parentTreeview) {
        parentTreeview.classList.add("menu-open");
        const parentLink = parentTreeview.querySelector(".nav-link");
        if (parentLink) parentLink.classList.add("active");
      }
    }
  });

  // Đảm bảo Dashboard được đánh dấu "active" nếu đang ở trang Dashboard
  if (currentPath === "/admin/dashboard") {
    const dashboardLink = document.querySelector(
      '.sidebar-menu a[href="/admin/dashboard"]'
    );
    if (dashboardLink) dashboardLink.classList.add("active");
  }
});
