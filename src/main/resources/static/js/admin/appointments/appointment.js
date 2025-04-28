
    $(document).ready(function() {
        let table = $('#appointmentTable').DataTable({
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
        
        // Status filter
        $('#statusFilter').on('change', function() {
            let status = $(this).val();
            table.column(6).search(status).draw();
        });
        
        // Type filter
        $('#typeFilter').on('change', function() {
            let type = $(this).val();
            table.column(2).search(type).draw();
        });
        
        // Date filter
        $('#dateFilter').on('change', function() {
            let date = $(this).val();
            if (date) {
                // Convert to DD/MM/YYYY format for search
                const parts = date.split('-');
                const formattedDate = parts[2] + '/' + parts[1] + '/' + parts[0];
                table.column(4).search(formattedDate).draw();
            } else {
                table.column(4).search('').draw();
            }
        });
        
        // Clear filters
        $('#clearFilters').on('click', function() {
            $('#statusFilter').val('');
            $('#typeFilter').val('');
            $('#dateFilter').val('');
            table.search('').columns().search('').draw();
        });
    });
