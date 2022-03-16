var updateBookDialogModal = document.getElementById('updateBookDialogModal');

updateBookDialogModal.addEventListener('show.bs.modal', function (event) {
    // Button that triggered the modal
    var button = event.relatedTarget;
    // Extract info from data-bs-* attributes
    var title = button.getAttribute('data-bs-title');
    var url = button.getAttribute('data-bs-url');
    // If necessary, you could initiate an AJAX request here
    // and then do the updating in a callback.
    //
    // Update the modal's content.
    var updateBookForm = updateBookDialogModal.querySelector('#updateBookForm');
    var titleInput = updateBookDialogModal.querySelector('#updateBookForm #title');

    updateBookForm.setAttribute('action', url);
    titleInput.value = title;
});

var deleteBookDialogModal = document.getElementById('deleteBookDialogModal');

deleteBookDialogModal.addEventListener('show.bs.modal', function (event) {
    // Button that triggered the modal
    var button = event.relatedTarget;
    // Extract info from data-bs-* attributes
    var title = button.getAttribute('data-bs-title');
    var url = button.getAttribute('data-bs-url');
    // If necessary, you could initiate an AJAX request here
    // and then do the updating in a callback.
    //
    // Update the modal's content.
    var deleteBookForm = deleteBookDialogModal.querySelector('#deleteBookForm');
    var deleteBookMessage = deleteBookDialogModal.querySelector('#deleteBookMessage');

    deleteBookForm.setAttribute('action', url);
    deleteBookMessage.innerHTML = '<b>' + title + '</b> will be deleted.';
});
