var deleteDiaryDialogModal = document.getElementById('deleteDiaryDialogModal');

deleteDiaryDialogModal.addEventListener('show.bs.modal', function (event) {
    // Button that triggered the modal
    var button = event.relatedTarget;
    // Extract info from data-bs-* attributes
    var title = button.getAttribute('data-bs-title');
    var url = button.getAttribute('data-bs-url');
    // If necessary, you could initiate an AJAX request here
    // and then do the updating in a callback.
    //
    // Update the modal's content.
    var deleteDiaryForm = deleteDiaryDialogModal.querySelector('#deleteDiaryForm');
    var deleteDiaryMessage = deleteDiaryDialogModal.querySelector('#deleteDiaryMessage');

    deleteDiaryForm.setAttribute('action', url);
    deleteDiaryMessage.innerHTML = '<b>' + title + '</b> will be deleted.';
});
