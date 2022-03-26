/*
    Variables pass from Thymeleaf in diary.js:
    const DIARY_BOOK_FRAGMENT_URL;
    const DIARY_RECORDS_FRAGMENT_URL;
*/

const TOAST_ID = '#toast';
const TOAST_DELAY = 2000; // milliseconds

const BOOK_ID_PREFIX = '#book_';
const BOOK_LINKS_ID = '#bookLinks';
const TITLE_INPUT_ID = '#titleInput';

const CREATE_BOOK_FORM_ID = '#createBookForm';

const UPDATE_BOOK_DIALOG_MODAL_ID = '#updateBookDialogModal';
const UPDATE_BOOK_FORM_ID = '#updateBookForm';

const DELETE_BOOK_DIALOG_MODAL_ID = '#deleteBookDialogModal';
const DELETE_BOOK_FORM_ID = '#deleteBookForm';
const DELETE_BOOK_MESSAGE_ID = '#deleteBookMessage';

const RECORDS_ID = '#records';
const LOAD_RECORDS_BUTTON_ID = '#loadRecordsButton'

const toast = new bootstrap.Toast($(TOAST_ID), {delay: TOAST_DELAY});
const toastBody = $(`${TOAST_ID} .toast-body`);
const bookLinks = $(BOOK_LINKS_ID);
const records = $(RECORDS_ID);

function setupAjaxFormSubmit(form, successHandler) {
    form.submit(function(event) {
        // avoid to execute the actual submit of the form.
        event.preventDefault();

        $.ajax({
            type: form.attr('method'),
            url: form.attr('action'),
            data: form.serialize(), // serializes the form's inputs.
            error: errorHandler,
            success: function(res) {
                console.log(res);

                // toast message
                toastBody.html(res.message);
                toast.show();

                // reset form and close dialog
                form[0].reset();
                form.closest('.modal').modal('hide');

                // update page
                successHandler(res);
            }
        });
    });
}

function errorHandler(jqXHR, textStatus, errorThrown) {
    console.error({
        jqXHR: jqXHR,
        textStatus: textStatus,
        errorThrown: errorThrown
    });
    const errorMessage = (jqXHR.responseJSON.error !== undefined) ? jqXHR.responseJSON.error : jqXHR.statusText;
    alert(`Error - ${jqXHR.status}: ${errorMessage}`);
}

/*
    Create Book
*/

const createBookForm = $(CREATE_BOOK_FORM_ID);

setupAjaxFormSubmit(createBookForm, function(res) {
    // load book fragment
    $.ajax({
        type: 'POST',
        url: DIARY_BOOK_FRAGMENT_URL,
        data: JSON.stringify(res.data),
        contentType: 'application/json',
        error: errorHandler,
        success: function(bookFragment) {
            // append new book fragment
            bookLinks.append(bookFragment);
        }
    });
});

/*
    Update Book
*/

const updateBookDialogModal = $(UPDATE_BOOK_DIALOG_MODAL_ID);
const updateBookForm = updateBookDialogModal.find(UPDATE_BOOK_FORM_ID);
const updateBookFormTitleInput = updateBookForm.find(TITLE_INPUT_ID);

updateBookDialogModal.on('show.bs.modal', function(event) {
    // Button that triggered the modal
    const button = event.relatedTarget;

    // Extract info from data-bs-* attributes
    const title = button.getAttribute('data-bs-title');
    const url = button.getAttribute('data-bs-url');

    // Update the modal's content.
    updateBookForm.attr('action', url);
    updateBookFormTitleInput.val(title);
});

setupAjaxFormSubmit(updateBookForm, function(res) {
    // load book fragment
    $.ajax({
        type: 'POST',
        url: DIARY_BOOK_FRAGMENT_URL,
        data: JSON.stringify(res.data),
        contentType: 'application/json',
        error: errorHandler,
        success: function(bookFragment) {
            // replace book fragment
            const BOOK_FRAGMENT_ID = BOOK_ID_PREFIX + res.data.id;
            bookLinks.find(BOOK_FRAGMENT_ID).replaceWith($(bookFragment).find(BOOK_FRAGMENT_ID));
        }
    });
});

/*
    Delete Book
*/

const deleteBookDialogModal = $(DELETE_BOOK_DIALOG_MODAL_ID);
const deleteBookForm = deleteBookDialogModal.find(DELETE_BOOK_FORM_ID);
const deleteBookMessage = deleteBookDialogModal.find(DELETE_BOOK_MESSAGE_ID);

deleteBookDialogModal.on('show.bs.modal', function(event) {
    // Button that triggered the modal
    const button = event.relatedTarget;

    // Extract info from data-bs-* attributes
    const title = button.getAttribute('data-bs-title');
    const url = button.getAttribute('data-bs-url');

    // Update the modal's content.
    deleteBookForm.attr('action', url);
    deleteBookMessage.html(`<b>${title}</b> will be deleted.`);
});

setupAjaxFormSubmit(deleteBookForm, function(res) {
    // delete book fragment
    const BOOK_FRAGMENT_ID = BOOK_ID_PREFIX + res.data.id;
    bookLinks.find(BOOK_FRAGMENT_ID).parent().remove();
});

/*
    Load more records
*/

const loadRecordsButton = $(LOAD_RECORDS_BUTTON_ID);
let currentBookId;
let totalPages;
let pageNumber;
let pageSize;

loadRecordsButton.on('click', function(event) {
    const button = event.currentTarget;

    // initialize
    if (currentBookId === undefined) {
        currentBookId = button.getAttribute('data-bs-current-book-id');
    }
    if (totalPages === undefined) {
        totalPages = parseInt(button.getAttribute('data-bs-total-pages'));
    }
    if (pageNumber === undefined) {
        pageNumber = parseInt(button.getAttribute('data-bs-page-number'));
    }
    if (pageSize === undefined) {
        pageSize = button.getAttribute('data-bs-page-size');
    }

    // load next page
    if (pageNumber+1 < totalPages) {
        $.ajax({
            type: 'GET',
            url: `/api/v1/diary/${currentBookId}/record/${pageNumber+1}/${pageSize}`,
            error: errorHandler,
            success: function(res) {
                console.log(res);

                // update page
                $.ajax({
                    type: 'POST',
                    url: DIARY_RECORDS_FRAGMENT_URL,
                    data: JSON.stringify(res.data.content),
                    contentType: 'application/json',
                    error: errorHandler,
                    success: function(recordsFragment) {
                        records.append($(recordsFragment).children());
                        pageNumber++;
                        if (pageNumber+1 >= totalPages) {
                            loadRecordsButton.hide();
                        }
                    }
                });
            }
        });
    } else {
        alert('There\'s no more.');
    }
});

loadRecordsButton.on('focus', function(event) {
    setTimeout(function() {
        event.currentTarget.blur();
    }, 200);
});