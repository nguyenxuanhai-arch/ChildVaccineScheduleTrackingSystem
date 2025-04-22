/**
 * Payment flow utility functions
 * Contains shared payment utility functions for the appointment and payment pages
 */

// Format currency to Vietnamese format
function formatCurrency(amount) {
  return new Intl.NumberFormat('vi-VN', { 
    style: 'currency', 
    currency: 'VND' 
  }).format(amount);
}

// Format date from ISO to Vietnamese format
function formatDate(dateString) {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
}

// Calculate age in months from date of birth
function calculateAgeInMonths(dob) {
  const birthDate = new Date(dob);
  const today = new Date();
  let months = (today.getFullYear() - birthDate.getFullYear()) * 12;
  months -= birthDate.getMonth();
  months += today.getMonth();
  if (today.getDate() < birthDate.getDate()) {
    months--;
  }
  return months;
}

// Create a payment through the API
function createPayment(paymentData, successCallback, errorCallback) {
  const token = localStorage.getItem('token');
  if (!token) {
    alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
    window.location.href = '/auth/login';
    return;
  }

  // Call API to create payment
  axios.post('/payments/create', paymentData, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    timeout: 10000 // 10 seconds timeout
  })
  .then(response => {
    if (response.data && response.data.success) {
      const paymentId = response.data.payment.id;
      
      // Call success callback
      if (typeof successCallback === 'function') {
        successCallback(response.data);
      }
    } else {
      throw new Error(response.data?.message || 'Không thể tạo thanh toán');
    }
  })
  .catch(error => {
    console.error('Error creating payment:', error);
    
    // Call error callback if provided
    if (typeof errorCallback === 'function') {
      errorCallback(error);
    } else {
      // Default error handling
      let errorMessage = 'Có lỗi xảy ra khi xử lý thanh toán. Vui lòng thử lại.';
      
      if (error.response?.status === 401) {
        errorMessage = 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.';
        setTimeout(() => {
          window.location.href = '/auth/login';
        }, 1500);
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      alert(errorMessage);
    }
  });
}

// Update payment status
function updatePaymentStatus(paymentId, status, notes, successCallback, errorCallback) {
  const token = localStorage.getItem('token');
  if (!token) {
    alert('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
    window.location.href = '/auth/login';
    return;
  }

  const data = {
    paymentId: paymentId,
    status: status,
    notes: notes
  };

  // Call API to update payment status
  axios.post('/payments/confirm', data, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    timeout: 10000 // 10 seconds timeout
  })
  .then(response => {
    if (response.data && response.data.success) {
      // Call success callback
      if (typeof successCallback === 'function') {
        successCallback(response.data);
      }
    } else {
      throw new Error(response.data?.message || 'Không thể cập nhật trạng thái thanh toán');
    }
  })
  .catch(error => {
    console.error('Error updating payment status:', error);
    
    // Call error callback if provided
    if (typeof errorCallback === 'function') {
      errorCallback(error);
    } else {
      // Default error handling
      let errorMessage = 'Có lỗi xảy ra khi cập nhật trạng thái thanh toán. Vui lòng thử lại.';
      
      if (error.response?.status === 401) {
        errorMessage = 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.';
        setTimeout(() => {
          window.location.href = '/auth/login';
        }, 1500);
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      alert(errorMessage);
    }
  });
}

// Format payment method for display
function formatPaymentMethod(method) {
  switch(method) {
    case 'CASH':
      return 'Tiền mặt';
    case 'BANK_TRANSFER':
      return 'Chuyển khoản';
    case 'CREDIT_CARD':
      return 'Thẻ tín dụng';
    case 'DEBIT_CARD':
      return 'Thẻ ghi nợ';
    case 'MOMO':
      return 'Ví MoMo';
    case 'ZALO_PAY':
      return 'ZaloPay';
    default:
      return method;
  }
}

// Format payment status for display
function formatPaymentStatus(status) {
  switch(status) {
    case 'PENDING':
      return 'Đang chờ';
    case 'COMPLETED':
      return 'Hoàn thành';
    case 'CONFIRMED':
      return 'Đã xác nhận';
    case 'CANCELLED':
      return 'Đã hủy';
    case 'FAILED':
      return 'Thất bại';
    case 'REFUNDED':
      return 'Đã hoàn tiền';
    case 'PROCESSING':
      return 'Đang xử lý';
    case 'EXPIRED':
      return 'Đã hết hạn';
    case 'DECLINED':
      return 'Bị từ chối';
    default:
      return status;
  }
} 