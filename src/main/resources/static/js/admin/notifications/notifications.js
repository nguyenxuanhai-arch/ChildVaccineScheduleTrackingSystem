$(document).ready(function() {
    // Khởi tạo DataTable
    const notificationTable = $('#notificationsTable').DataTable({
        "ajax": {
          "url": "/admin/notifications/data",
          "dataSrc": ""
        },
        "columns": [
          { "data": "id" },
          { 
            "data": "type",
            "render": function(data) {
              let badge = '';
              switch(data) {
                case 'VACCINE':
                  badge = '<span class="badge bg-success">Tiêm lẻ</span>';
                  break;
                case 'PACKAGE':
                  badge = '<span class="badge bg-warning">Tiêm gói</span>';
                  break;
                case 'PAYMENT':
                  badge = '<span class="badge bg-primary">Thanh toán</span>';
                  break;
                default:
                  badge = '<span class="badge bg-secondary">Hệ thống</span>';
              }
              return badge;
            }
          },
          { 
            "data": "userId",
            "render": function(data) {
              return data ? data : '<em>Tất cả</em>';
            }
          },
          { 
            "data": "title",
            "render": function(data) {
              return data || '<em>Không có tiêu đề</em>';
            }
          },
          { 
            "data": "message",
            "render": function(data) {
              if (!data) return '<em>Không có nội dung</em>';
              // Limit the message length in the table
              return data.length > 50 ? data.substring(0, 50) + '...' : data;
            }
          },
          { 
            "data": "sentAt",
            "render": function(data) {
              if (!data) return '<em>N/A</em>';
              try {
                const date = new Date(data);
                return date.toLocaleString('vi-VN');
              } catch (e) {
                console.error('Error formatting date:', e);
                return '<em>Không hợp lệ</em>';
              }
            }
          },
          {
            "data": "status",
            "render": function(data) {
              return data ? 
                '<span class="badge bg-success">Đã đọc</span>' : 
                '<span class="badge bg-danger">Chưa đọc</span>';
            }
          },
          {
            "data": "id",
            "render": function(data) {
              return `
                <div class="btn-group">
                  <button type="button" class="btn btn-sm btn-info view-btn" data-id="${data}">
                    <i class="bi bi-eye"></i>
                  </button>
                  <button type="button" class="btn btn-sm btn-danger delete-btn" data-id="${data}">
                    <i class="bi bi-trash"></i>
                  </button>
                </div>
              `;
            }
          }
        ],
        "paging": true,
        "lengthChange": true,
        "searching": true,
        "ordering": true,
        "info": true,
        "autoWidth": false,
        "responsive": true,
        "language": {
          "search": "Tìm kiếm:",
          "lengthMenu": "Hiển thị _MENU_ mục",
          "info": "Hiển thị _START_ đến _END_ của _TOTAL_ mục",
          "infoEmpty": "Hiển thị 0 đến 0 của 0 mục",
          "infoFiltered": "(được lọc từ _MAX_ mục)",
          "paginate": {
            "first": "Đầu",
            "last": "Cuối",
            "next": "Tiếp",
            "previous": "Trước"
          },
          "emptyTable": "Không có dữ liệu"
        }
      });

    // Load danh sách người dùng
    function loadUsers() {
        $.ajax({
            url: '/admin/notifications/users',
            method: 'GET',
            success: function(users) {
                const userSelect = $('#userId');
                userSelect.empty();
                userSelect.append('<option value="">Chọn người dùng...</option>');
                users.forEach(user => {
                    const displayName = user.name ? `${user.name} (${user.username})` : user.username;
                    userSelect.append(`<option value="${user.id}">${displayName}</option>`);
                });
            },
            error: function(xhr, status, error) {
                console.error('Error loading users:', error);
                Swal.fire('Lỗi!', 'Không thể tải danh sách người dùng', 'error');
            }
        });
    }

    // Xử lý radio buttons người nhận
    $('input[name="recipientType"]').change(function() {
        if ($(this).val() === 'specific') {
            $('#userSelect').removeClass('d-none');
            $('#userId').prop('required', true);
        } else {
            $('#userSelect').addClass('d-none');
            $('#userId').prop('required', false);
        }
    });

    // Xử lý nút template trên trang chính
    $('.template-card .template-btn').click(function() {
        const type = $(this).data('type');
        const title = $(this).data('title');
        const template = $(this).data('template');
        
        // Mở modal và điền thông tin
        $('#createNotificationModal').modal('show');
        $('#notificationType').val(type);
        $('#notificationTitle').val(title);
        $('#notificationContent').val(template);
    });

    // Load users when modal opens
    $('#createNotificationModal').on('show.bs.modal', function() {
        loadUsers();
    });

    // Reset form when modal closes
    $('#createNotificationModal').on('hidden.bs.modal', function() {
        $('#createNotificationForm')[0].reset();
        $('#userSelect').addClass('d-none');
        $('#userId').prop('required', false);
    });

    // Xử lý nút Lưu & Gửi
    $('#saveNotification').click(function() {
        const form = $('#createNotificationForm')[0];
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        const type = $('#notificationType').val();
        const title = $('#notificationTitle').val();
        const message = $('#notificationContent').val();
        const recipientType = $('input[name="recipientType"]:checked').val();
        const sendEmail = $('#sendEmailCheckbox').is(':checked');

        $(this).prop('disabled', true).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Đang gửi...');

        if (recipientType === 'all') {
            // Gửi cho tất cả người dùng
            $.ajax({
                url: '/admin/notifications/send-to-all',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    type: type,
                    title: title,
                    message: message,
                    sendEmail: sendEmail
                }),
                success: function(response) {
                    Swal.fire('Thành công!', response.message, 'success');
                    $('#createNotificationModal').modal('hide');
                    notificationTable.ajax.reload();
                },
                error: function(xhr) {
                    const errorMsg = xhr.responseJSON ? xhr.responseJSON.message : 'Đã xảy ra lỗi khi gửi thông báo';
                    Swal.fire('Lỗi!', errorMsg, 'error');
                },
                complete: function() {
                    $('#saveNotification').prop('disabled', false).html('Lưu & Gửi');
                }
            });
        } else {
            // Gửi cho người dùng cụ thể
            const userId = $('#userId').val();
            if (!userId) {
                Swal.fire('Lỗi!', 'Vui lòng chọn người dùng.', 'error');
                $('#saveNotification').prop('disabled', false).html('Lưu & Gửi');
                return;
            }
            
            $.ajax({
                url: '/admin/notifications/send',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    userId: userId,
                    type: type,
                    title: title,
                    message: message,
                    sendEmail: sendEmail
                }),
                success: function(response) {
                    Swal.fire('Thành công!', response.message, 'success');
                    $('#createNotificationModal').modal('hide');
                    notificationTable.ajax.reload();
                },
                error: function(xhr) {
                    const errorMsg = xhr.responseJSON ? xhr.responseJSON.message : 'Đã xảy ra lỗi khi gửi thông báo';
                    Swal.fire('Lỗi!', errorMsg, 'error');
                },
                complete: function() {
                    $('#saveNotification').prop('disabled', false).html('Lưu & Gửi');
                }
            });
        }
    });

      // Delete notification
      $(document).on('click', '.delete-btn', function() {
        const id = $(this).data('id');
        
        Swal.fire({
          title: 'Xác nhận xóa?',
          text: 'Bạn có chắc chắn muốn xóa thông báo này không?',
          icon: 'warning',
          showCancelButton: true,
          confirmButtonColor: '#d33',
          cancelButtonColor: '#3085d6',
          confirmButtonText: 'Xóa',
          cancelButtonText: 'Hủy'
        }).then((result) => {
          if (result.isConfirmed) {
            $.ajax({
              url: '/admin/notifications/' + id,
              type: 'DELETE',
              success: function(response) {
                Swal.fire('Đã xóa!', response.message, 'success');
                notificationTable.ajax.reload();
              },
              error: function(error) {
                Swal.fire('Lỗi!', 'Không thể xóa thông báo.', 'error');
              }
            });
          }
        });
      });

      // View notification
      $(document).on('click', '.view-btn', function() {
        const row = notificationTable.row($(this).closest('tr')).data();
        
        let statusBadge = row.status ? 
          '<span class="badge bg-success">Đã đọc</span>' : 
          '<span class="badge bg-danger">Chưa đọc</span>';
        
        let typeBadge = '';
        switch(row.type) {
          case 'VACCINE':
            typeBadge = '<span class="badge bg-success">Tiêm lẻ</span>';
            break;
          case 'PACKAGE':
            typeBadge = '<span class="badge bg-warning">Tiêm gói</span>';
            break;
          case 'PAYMENT':
            typeBadge = '<span class="badge bg-primary">Thanh toán</span>';
            break;
          default:
            typeBadge = '<span class="badge bg-secondary">Hệ thống</span>';
        }
        
        // Format date properly
        let formattedDate = 'N/A';
        if (row.sentAt) {
          try {
            const date = new Date(row.sentAt);
            formattedDate = date.toLocaleString('vi-VN');
          } catch (e) {
            console.error('Error formatting date:', e);
          }
        }
        
        Swal.fire({
          title: row.title || 'Thông báo',
          html: `
            <div class="text-start">
              <p><strong>Loại:</strong> ${typeBadge}</p>
              <p><strong>Người nhận ID:</strong> ${row.userId || 'N/A'}</p>
              <p><strong>Nội dung:</strong> ${row.message || 'Không có nội dung'}</p>
              <p><strong>Thời gian gửi:</strong> ${formattedDate}</p>
              <p><strong>Trạng thái:</strong> ${statusBadge}</p>
            </div>
          `,
          icon: 'info',
          confirmButtonText: 'Đóng'
        });
      });

      // Filter button click
      $('.filter-btn').on('click', function() {
        $('.filter-btn').removeClass('active');
        $(this).addClass('active');
        
        const filter = $(this).data('filter');
        if (filter === 'all') {
          notificationTable.column(1).search('').draw();
        } else {
          notificationTable.column(1).search(filter).draw();
        }
      });

      // Filter link click
      $('.filter-link').on('click', function(e) {
        e.preventDefault();
        const filter = $(this).data('filter');
        $('.filter-btn').removeClass('active');
        $('.filter-btn[data-filter="' + filter + '"]').addClass('active');
        notificationTable.column(1).search(filter).draw();
      });

      // Hiển thị/ẩn các trường tùy chọn trong modal
      $('#notificationRecipients').on('change', function() {
        if ($(this).val() === 'specific') {
          $('#recipientsSpecificGroup').removeClass('d-none');
        } else {
          $('#recipientsSpecificGroup').addClass('d-none');
        }
      });

      $('#notificationSchedule').on('change', function() {
        if ($(this).val() === 'later') {
          $('#scheduleTimeGroup').removeClass('d-none');
        } else {
          $('#scheduleTimeGroup').addClass('d-none');
        }
      });
    });
