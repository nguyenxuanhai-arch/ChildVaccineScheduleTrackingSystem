// Global variables
let paymentDetailsModal;
let statusUpdateModal;
let paymentTable;

// Function to show status update modal
function showStatusUpdateModal(paymentId) {
    try {
        const csrfToken = $('meta[name="_csrf"]').attr('content');
        const csrfParameter = $('meta[name="_csrf_parameter"]').attr('content') || '_csrf';
        
        $('#modalPaymentIdForStatus').val(paymentId);
        $('#modalCsrfToken').attr('name', csrfParameter).val(csrfToken);
        
        if (statusUpdateModal) {
            statusUpdateModal.show();
        } else {
            const modalElement = document.getElementById('statusUpdateModal');
            if (modalElement) {
                statusUpdateModal = new bootstrap.Modal(modalElement);
                statusUpdateModal.show();
            }
        }
    } catch (error) {
        console.error("Error showing status update modal:", error);
        showToast('Error', 'Failed to show status update modal', 'error');
    }
}

// Function to show payment details
function togglePaymentDetails(paymentId) {
    try {
        if (!paymentDetailsModal) {
            const modalElement = document.getElementById('paymentDetailsModal');
            if (modalElement) {
                paymentDetailsModal = new bootstrap.Modal(modalElement);
            }
        }
        
        if (paymentDetailsModal) {
            $('#modalPaymentId').text(paymentId);
            const detailsButton = $(`button[data-payment-id="${paymentId}"]`);
            const notes = detailsButton.length ? detailsButton.data('notes') || 'No notes' : 'No notes';
            $('#modalPaymentNotes').text(notes);
            paymentDetailsModal.show();
        }
    } catch (error) {
        console.error("Error showing payment details:", error);
        showToast('Error', 'Failed to show payment details', 'error');
    }
}

// Function to show toast messages
function showToast(title, message, type) {
    try {
        if (!$('#toastContainer').length) {
            $('body').append('<div id="toastContainer" style="position: fixed; top: 20px; right: 20px; z-index: 1050;"></div>');
        }
        
        const toastId = 'toast-' + new Date().getTime();
        const bgClass = type === 'success' ? 'bg-success' : 'bg-danger';
        
        const toastHtml = `
            <div id="${toastId}" class="toast ${bgClass} text-white" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="toast-header">
                    <strong class="me-auto">${title}</strong>
                    <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
                <div class="toast-body">
                    ${message}
                </div>
            </div>
        `;
        
        $('#toastContainer').append(toastHtml);
        const toastElement = new bootstrap.Toast(document.getElementById(toastId), { delay: 5000 });
        toastElement.show();
    } catch (error) {
        console.error("Error showing toast:", error);
    }
}

// Initialize when document is ready
$(document).ready(function() {
    try {
        // Initialize modals
        const modalElements = document.querySelectorAll('.modal');
        modalElements.forEach(function(element) {
            try {
                new bootstrap.Modal(element);
            } catch (e) {
                console.warn("Could not initialize modal:", element.id, e);
            }
        });

        // Initialize DataTable
        paymentTable = $('#paymentTable').DataTable({
            paging: true,
            lengthChange: true,
            searching: true,
            ordering: true,
            info: true,
            autoWidth: false,
            responsive: true,
            language: {
                search: "Search:",
                lengthMenu: "Show _MENU_ entries",
                info: "Showing _START_ to _END_ of _TOTAL_ entries",
                infoEmpty: "Showing 0 to 0 of 0 entries",
                infoFiltered: "(filtered from _MAX_ total entries)",
                paginate: {
                    first: "First",
                    last: "Last",
                    next: "Next",
                    previous: "Previous"
                },
                emptyTable: "No data available"
            }
        });

        // Toggle between simple and detailed view
        $('#toggleSimpleView').on('change', function() {
            if(this.checked) {
                $('#simpleView').show();
                $('#detailedView').hide();
                $('#simpleView table').DataTable().draw();
            } else {
                $('#simpleView').hide();
                $('#detailedView').show();
                paymentTable.draw();
            }
        });

        // Status filter
        $('#statusFilter').on('change', function() {
            const status = $(this).val();
            if (paymentTable) {
                paymentTable.column(6).search(status).draw();
            }
            $('#simpleView table').DataTable().column(5).search(status).draw();
        });

        // Payment method filter
        $('#paymentMethodFilter').on('change', function() {
            const method = $(this).val();
            if (paymentTable) {
                paymentTable.column(5).search(method).draw();
            }
            $('#simpleView table').DataTable().column(4).search(method).draw();
        });

        // Status dropdown change handler
        $(document).on('change', '.status-dropdown', function() {
            const dropdown = $(this);
            const paymentId = dropdown.data('payment-id');
            const newStatus = dropdown.val();
            const currentStatus = dropdown.data('current-status');
            
            if (newStatus === currentStatus) {
                return;
            }
            
            const spinner = dropdown.siblings('.status-spinner');
            spinner.removeClass('d-none');
            dropdown.prop('disabled', true);
            
            const csrfToken = $('meta[name="_csrf"]').attr('content');
            const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
            
            $.ajax({
                url: `/admin/payments/${paymentId}/status`,
                type: 'POST',
                headers: {
                    [csrfHeader]: csrfToken
                },
                data: {
                    status: newStatus
                },
                success: function(response) {
                    if (response.success) {
                        showToast('Success', response.message, 'success');
                        dropdown.data('current-status', newStatus);
                        $(`.payment-status-badge[data-payment-id="${paymentId}"]`).text(newStatus);
                    } else {
                        showToast('Error', response.message || 'Failed to update status', 'error');
                        dropdown.val(currentStatus);
                    }
                },
                error: function(xhr) {
                    console.error('Error updating payment status:', xhr);
                    const errorMsg = xhr.responseJSON?.message || 'Error updating payment status';
                    showToast('Error', errorMsg, 'error');
                    dropdown.val(currentStatus);
                },
                complete: function() {
                    spinner.addClass('d-none');
                    dropdown.prop('disabled', false);
                }
            });
        });

    } catch (error) {
        console.error("Error initializing payment management:", error);
        showToast('Error', 'Failed to initialize payment management', 'error');
    }
}); 