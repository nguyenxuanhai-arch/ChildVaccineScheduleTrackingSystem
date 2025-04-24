
        $(document).ready(function() {
            // Handle cancel appointment button clicks
            $('.cancel-appointment').click(function() {
                if (confirm('Bạn có chắc chắn muốn hủy lịch tiêm này không?')) {
                    const appointmentId = $(this).data('id');
                    
                    // Get the JWT token from localStorage
                    const token = localStorage.getItem('token');
                    
                    // Send request to cancel the appointment
                    $.ajax({
                        url: '/appointments/' + appointmentId + '/cancel',
                        type: 'PUT',
                        headers: {
                            'Authorization': 'Bearer ' + token
                        },
                        success: function() {
                            // Reload the page to reflect the changes
                            location.reload();
                        },
                        error: function(xhr) {
                            alert('Không thể hủy lịch tiêm: ' + (xhr.responseText || 'Lỗi không xác định'));
                        }
                    });
                }
            });
        });
