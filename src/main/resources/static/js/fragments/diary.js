/* const DIARY_BOOK_FRAGMENT_URL passed from Thymeleaf in fragments/diary.html */

const BOOK_ID_PREFIX = '#book_';
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

function setupAjaxFormSubmit(form, successHandler) {
    form.submit(function(e) {
        // avoid to execute the actual submit of the form.
        e.preventDefault();

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

const createBookDialogModal = $(CREATE_BOOK_DIALOG_MODAL_ID);
const createBookForm = $(CREATE_BOOK_FORM_ID);

setupAjaxFormSubmit(createBookForm, function(res) {
    console.log(res);

    // load book fragment
    $.ajax({
        type: 'post',
        url: DIARY_BOOK_FRAGMENT_URL,
        data: JSON.stringify(res.data),
        contentType: "application/json",
        error: errorHandler,
        success: function(bookFragment) {
            createBookForm[0].reset(); // clear inputs
            createBookDialogModal.modal('hide'); // close dialog
            $(BOOK_LINKS_ID).append(bookFragment); // append new book fragment
        }
    });
});

/*
    Update Book
*/

const updateBookDialogModal = $(UPDATE_BOOK_DIALOG_MODAL_ID);
const updateBookForm = updateBookDialogModal.find(UPDATE_BOOK_FORM_ID);
const titleInput = updateBookForm.find(TITLE_INPUT_ID);

updateBookDialogModal.on('show.bs.modal', function (event) {
    // Button that triggered the modal
    const button = event.relatedTarget;

    // Extract info from data-bs-* attributes
    const title = button.getAttribute('data-bs-title');
    const url = button.getAttribute('data-bs-url');

    // Update the modal's content.
    updateBookForm.attr('action', url);
    titleInput.val(title);
});

setupAjaxFormSubmit(updateBookForm, function(res) {
    console.log(res);

    // load book fragment
    $.ajax({
        type: 'post',
        url: DIARY_BOOK_FRAGMENT_URL,
        data: JSON.stringify(res.data),
        contentType: "application/json",
        error: errorHandler,
        success: function(bookFragment) {
            updateBookDialogModal.modal('hide'); // close dialog
            // replace book fragment
            const BOOK_FRAGMENT_ID = BOOK_ID_PREFIX + res.data.id;
            $(BOOK_LINKS_ID).find(BOOK_FRAGMENT_ID)[0].replaceWith($(bookFragment).find(BOOK_FRAGMENT_ID)[0]);
        }
    });
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
