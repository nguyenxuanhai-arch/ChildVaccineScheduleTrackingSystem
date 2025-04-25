
$(document).ready(function() {
  // Tải thông tin thanh toán nếu cần
  // loadPaymentInfo();
  
  // Set min date for appointment date to today
  const today = new Date().toISOString().split('T')[0];
  document.getElementById('appointmentDate').min = today;
  
  // Thêm sự kiện alert khi lưu form cập nhật lịch hẹn cho gói tiêm
  $('form[action*="appointments/save"]').on('submit', function() {
    if ($('#sendNotification').is(':checked')) {
      // Chỉ hiển thị thông báo nếu tùy chọn gửi thông báo được chọn
      alert('Hệ thống sẽ tự động gửi thông báo đến khách hàng về lịch hẹn tiêm vừa được cập nhật.');
    }
  });
});
