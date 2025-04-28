$(document).ready(function() {
    // Khởi tạo DataTable
    const table = $('#feedbackTable').DataTable({
        "processing": true,
        "serverSide": false,
        "ajax": {
            "url": "/admin/feedbacks/data",
            "type": "GET",
            "dataSrc": function(json) {
                if (json.error) {
                    console.error('Error loading data:', json.error);
                    return [];
                }
                return json.data || [];
            },
            "error": function(xhr, error, thrown) {
                console.error('DataTables error:', error, thrown);
                Swal.fire({
                    icon: 'error',
                    title: 'Lỗi tải dữ liệu',
                    text: 'Không thể tải dữ liệu đánh giá. Vui lòng thử lại sau.'
                });
            }
        },
        "columns": [
            { 
                "data": null,
                "orderable": false,
                "defaultContent": '<input type="checkbox" class="form-check-input checkbox-item">'
            },
            { 
                "data": null,
                "render": function(data) {
                    if (!data.username && !data.name) return '<em>Không xác định</em>';
                    return data.name || data.username || '<em>Không xác định</em>';
                }
            },
            { 
                "data": "rating",
                "render": function(data) {
                    if (!data) return '<em>0</em>';
                    let stars = '';
                    for (let i = 0; i < 5; i++) {
                        if (i < data) {
                            stars += '<i class="bi bi-star-fill text-warning"></i>';
                        } else {
                            stars += '<i class="bi bi-star text-warning"></i>';
                        }
                    }
                    return stars;
                }
            },
            { 
                "data": "message",
                "render": function(data) {
                    if (!data) return '<em>Không có nội dung</em>';
                    return data.length > 100 ? data.substring(0, 100) + '...' : data;
                }
            },
            {
                "data": "createdAt",
                "render": function(data) {
                    if (!data) return '<em>N/A</em>';
                    try {
                        const date = new Date(data);
                        return date.toLocaleDateString('vi-VN', {
                            year: 'numeric',
                            month: '2-digit',
                            day: '2-digit',
                            hour: '2-digit',
                            minute: '2-digit'
                        });
                    } catch (e) {
                        console.error('Error parsing date:', e);
                        return '<em>Không hợp lệ</em>';
                    }
                }
            },
            {
                "data": "id",
                "render": function(data) {
                    return `
                        <div class="btn-group">
                            <a href="/admin/feedbacks/view/${data}" class="btn btn-sm btn-primary">
                                <i class="bi bi-eye"></i>
                            </a>
                            <button type="button" class="btn btn-sm btn-danger delete-btn" data-id="${data}">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    `;
                }
            }
        ],
        "order": [[4, "desc"]], // Sắp xếp theo ngày tạo mới nhất
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

    // Xử lý checkbox "chọn tất cả"
    $('#checkAll').on('change', function() {
        $('.checkbox-item').prop('checked', $(this).prop('checked'));
    });

    // Xử lý nút xóa
    $(document).on('click', '.delete-btn', function() {
        const id = $(this).data('id');
        if (!id) return;

        Swal.fire({
            title: 'Xác nhận xóa?',
            text: "Bạn có chắc chắn muốn xóa đánh giá này không?",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Xóa',
            cancelButtonText: 'Hủy'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `/admin/feedbacks/delete/${id}`,
                    type: 'POST',
                    success: function() {
                        Swal.fire(
                            'Đã xóa!',
                            'Đánh giá đã được xóa thành công.',
                            'success'
                        ).then(() => {
                            table.ajax.reload();
                        });
                    },
                    error: function(xhr) {
                        Swal.fire(
                            'Lỗi!',
                            'Không thể xóa đánh giá. ' + (xhr.responseJSON?.message || 'Đã có lỗi xảy ra.'),
                            'error'
                        );
                    }
                });
            }
        });
    });

    // Xử lý bộ lọc
    $('#ratingFilter, #statusFilter, #dateFrom, #dateTo').on('change', function() {
        table.draw();
    });

    // Custom filtering function
    $.fn.dataTable.ext.search.push(function(settings, data, dataIndex) {
        const rating = parseInt($('#ratingFilter').val());
        const dateFrom = $('#dateFrom').val();
        const dateTo = $('#dateTo').val();

        // Rating filter
        if (rating) {
            const rowRating = (data[2].match(/bi-star-fill/g) || []).length;
            if (rowRating !== rating) return false;
        }

        // Date filter
        if (dateFrom || dateTo) {
            const rowDate = new Date(data[4]);
            if (dateFrom && rowDate < new Date(dateFrom)) return false;
            if (dateTo && rowDate > new Date(dateTo)) return false;
        }

        return true;
    });
}); 