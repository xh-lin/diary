const FRAGMENT_URL_ATTR_NAME = 'fragment-url';
const BOOK_LINKS_ID = '#bookLinks';
const TITLE_INPUT_ID = '#titleInput';
const CREATE_BOOK_DIALOG_MODAL_ID = "#createBookDialogModal";
const CREATE_BOOK_FORM_ID = '#createBookForm';
const UPDATE_BOOK_DIALOG_MODAL_ID = "#updateBookDialogModal";
const UPDATE_BOOK_FORM_ID = '#updateBookForm';
const DELETE_BOOK_DIALOG_MODAL_ID = "#deleteBookDialogModal";
const DELETE_BOOK_FORM_ID = '#deleteBookForm';
const DELETE_BOOK_MESSAGE_ID = '#deleteBookMessage';

function errorHandler(jqXHR, textStatus, errorThrown) {
    console.error(jqXHR, textStatus, errorThrown);
    alert('Error - ' + jqXHR.status + ': ' + jqXHR.statusText);
}

function ajaxSubmitForm(formId, successHandler) {
    const form = $(formId);

    form.submit(function(e) {
        e.preventDefault(); // avoid to execute the actual submit of the form.

        $.ajax({
            type: form.attr('method'),
            url: form.attr('action'),
            data: form.serialize(), // serializes the form's inputs.
            error: errorHandler,
            success: successHandler
        });
    });
}

/*
    Create Book
*/

ajaxSubmitForm(CREATE_BOOK_FORM_ID, function(res) {
    console.log(res);
    const form = $(CREATE_BOOK_FORM_ID);

    $.ajax({
        type: 'post',
        url: form.attr(FRAGMENT_URL_ATTR_NAME),
        data: JSON.stringify(res.data),
        contentType: "application/json",
        error: errorHandler,
        success: function(bookFragment) {
            form[0].reset(); // clear inputs
            $(CREATE_BOOK_DIALOG_MODAL_ID).modal('hide');
            $(BOOK_LINKS_ID).append(bookFragment);
        }
    });
});

/*
    Update Book
*/

const updateBookDialogModal = $(UPDATE_BOOK_DIALOG_MODAL_ID)[0];

updateBookDialogModal.addEventListener('show.bs.modal', function (event) {
    // Button that triggered the modal
    const button = event.relatedTarget;
    // Extract info from data-bs-* attributes
    const title = button.getAttribute('data-bs-title');
    const url = button.getAttribute('data-bs-url');
    // If necessary, you could initiate an AJAX request here
    // and then do the updating in a callback.
    //
    // Update the modal's content.
    const updateBookForm = updateBookDialogModal.querySelector(UPDATE_BOOK_FORM_ID);
    const titleInput = updateBookForm.querySelector(TITLE_INPUT_ID);

    updateBookForm.setAttribute('action', url);
    titleInput.value = title;
});

/*
    Delete Book
*/

const deleteBookDialogModal = $(DELETE_BOOK_DIALOG_MODAL_ID)[0];

deleteBookDialogModal.addEventListener('show.bs.modal', function (event) {
    // Button that triggered the modal
    const button = event.relatedTarget;
    // Extract info from data-bs-* attributes
    const title = button.getAttribute('data-bs-title');
    const url = button.getAttribute('data-bs-url');
    // If necessary, you could initiate an AJAX request here
    // and then do the updating in a callback.
    //
    // Update the modal's content.
    const deleteBookForm = deleteBookDialogModal.querySelector(DELETE_BOOK_FORM_ID);
    const deleteBookMessage = deleteBookDialogModal.querySelector(DELETE_BOOK_MESSAGE_ID);

    deleteBookForm.setAttribute('action', url);
    deleteBookMessage.innerHTML = '<b>' + title + '</b> will be deleted.';
});
