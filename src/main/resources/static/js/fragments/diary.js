/*
    Variables pass from Thymeleaf in fragments/diary::scripts:

    const DIARY_URL;
    const DIARY_BOOK_FRAGMENT_URL;
    const DIARY_RECORDS_FRAGMENT_URL;
    let currentBookId;
*/
console.assert(DIARY_URL !== undefined);
console.assert(DIARY_BOOK_FRAGMENT_URL !== undefined);
console.assert(DIARY_RECORDS_FRAGMENT_URL !== undefined);
console.assert(currentBookId !== undefined);

const BOOK_ID_PREFIX = '#book_';
const BOOK_LINKS_ID = '#bookLinks';
const TITLE_INPUT_ID = '#titleInput';

const CREATE_BOOK_FORM_ID = '#createBookForm';

const UPDATE_BOOK_DIALOG_MODAL_ID = '#updateBookDialogModal';
const UPDATE_BOOK_FORM_ID = '#updateBookForm';

const DELETE_BOOK_DIALOG_MODAL_ID = '#deleteBookDialogModal';
const DELETE_BOOK_FORM_ID = '#deleteBookForm';
const DELETE_BOOK_MESSAGE_ID = '#deleteBookMessage';

const RECORD_ID_PREFIX = '#record_';
const RECORDS_ID = '#records';
const LOAD_RECORDS_FORM_ID = '#loadRecordsForm'

const CREATE_RECORD_FORM_ID = '#createRecordForm';

const UPDATE_RECORD_DIALOG_MODAL_ID = '#updateRecordDialogModal';
const UPDATE_RECORD_FORM_ID = '#updateRecordForm';

const DELETE_RECORD_DIALOG_MODAL_ID = '#deleteRecordDialogModal';
const DELETE_RECORD_FORM_ID = '#deleteRecordForm';
const DELETE_RECORD_MESSAGE_ID = '#deleteRecordMessage';

const bookLinks = $(BOOK_LINKS_ID);
const records = $(RECORDS_ID);

/*
    Toast in the beginning
*/

const toastMessage = getUrlParam('toast');
if (toastMessage !== undefined) {
    showToast(toastMessage);
}

/*
    Convert records timestamps to local timezone
*/

formatTimestamps(records);

/*
    Create Book
*/

const createBookForm = $(CREATE_BOOK_FORM_ID);

setupAjaxFormSubmit(createBookForm, function (res) {
    // load book fragment
    $.ajax({
        type: 'POST',
        url: DIARY_BOOK_FRAGMENT_URL,
        data: JSON.stringify(res.data),
        contentType: 'application/json',
        error: errorHandler,
        success: function (bookFragment) {
            if (currentBookId === null) {
                redirect(DIARY_URL, { toast: res.message });
            } else {
                // append new book fragment
                bookLinks.append(bookFragment);
            }
        }
    });
});

/*
    Update Book
*/

const updateBookDialogModal = $(UPDATE_BOOK_DIALOG_MODAL_ID);
const updateBookForm = updateBookDialogModal.find(UPDATE_BOOK_FORM_ID);
const updateBookFormTitleInput = updateBookForm.find(TITLE_INPUT_ID);

updateBookDialogModal.on('show.bs.modal', function (event) {
    // Button that triggered the modal
    const button = event.relatedTarget;

    // Extract info from data-bs-* attributes
    const title = button.getAttribute('data-bs-title');
    const url = button.getAttribute('data-bs-url');

    // Update the modal's content.
    updateBookForm.attr('action', url);
    updateBookFormTitleInput.val(title);
});

setupAjaxFormSubmit(updateBookForm, function (res) {
    // load book fragment
    $.ajax({
        type: 'POST',
        url: DIARY_BOOK_FRAGMENT_URL,
        data: JSON.stringify(res.data),
        contentType: 'application/json',
        error: errorHandler,
        success: function (bookFragment) {
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

deleteBookDialogModal.on('show.bs.modal', function (event) {
    // Button that triggered the modal
    const button = event.relatedTarget;

    // Extract info from data-bs-* attributes
    const title = button.getAttribute('data-bs-title');
    const url = button.getAttribute('data-bs-url');

    // Update the modal's content.
    deleteBookForm.attr('action', url);
    deleteBookMessage.html(`<b>${title}</b> will be deleted.`);
});

setupAjaxFormSubmit(deleteBookForm, function (res) {
    const BOOK_FRAGMENT_ID = BOOK_ID_PREFIX + res.data.id;
    const bookLink = bookLinks.find(BOOK_FRAGMENT_ID);
    // if deleting a selected book
    if (res.data.id === currentBookId) {
        // redirect to default book page with toast message on page loaded
        redirect(DIARY_URL, { toast: res.message });
    } else {
        // delete book fragment
        bookLink.parent().remove();
    }
});

/*
    Load more records
*/

const loadRecordsForm = $(LOAD_RECORDS_FORM_ID);

setupAjaxFormSubmit(loadRecordsForm, function (res) {
    // load records fragment
    $.ajax({
        type: 'POST',
        url: DIARY_RECORDS_FRAGMENT_URL,
        data: JSON.stringify(res.data.content),
        contentType: 'application/json',
        error: errorHandler,
        success: function (recordsFragment) {
            const recordsHtml = $(recordsFragment);
            formatTimestamps(recordsHtml);
            records.append(recordsHtml.children());

            loadRecordsForm.attr('action', res.data.nextPageUrl);
            if (loadRecordsForm.attr('action') === undefined) {
                loadRecordsForm.hide();
            }
        }
    });
});

loadRecordsForm.find('button:submit').on('focus', function (event) {
    setTimeout(function () {
        event.currentTarget.blur();
    }, 200);
});

/*
    Create record
*/

const createRecordForm = $(CREATE_RECORD_FORM_ID);

setupAjaxFormSubmit(createRecordForm, function (res) {
    // load records fragment
    $.ajax({
        type: 'POST',
        url: DIARY_RECORDS_FRAGMENT_URL,
        data: JSON.stringify([res.data]),
        contentType: 'application/json',
        error: errorHandler,
        success: function (recordsFragment) {
            const recordsHtml = $(recordsFragment);
            formatTimestamps(recordsHtml);
            records.prepend(recordsHtml.children());
        }
    });
});

/*
    Update record
*/

const updateRecordDialogModal = $(UPDATE_RECORD_DIALOG_MODAL_ID);
const updateRecordForm = updateRecordDialogModal.find(UPDATE_RECORD_FORM_ID);
const updateRecordFormTextarea = updateRecordForm.find('textarea');

updateRecordDialogModal.on('show.bs.modal', function (event) {
    // Button that triggered the modal
    const button = event.relatedTarget;

    // Extract info from data-bs-* attributes
    const text = button.getAttribute('data-bs-text');
    const url = button.getAttribute('data-bs-url');

    // Update the modal's content.
    updateRecordForm.attr('action', url);
    updateRecordFormTextarea.val(text);
});

setupAjaxFormSubmit(updateRecordForm, function (res) {
    // load records fragment
    $.ajax({
        type: 'POST',
        url: DIARY_RECORDS_FRAGMENT_URL,
        data: JSON.stringify([res.data]),
        contentType: 'application/json',
        error: errorHandler,
        success: function (recordsFragment) {
            const recordsHtml = $(recordsFragment);
            formatTimestamps(recordsHtml);
            // replace record fragment
            const RECORD_FRAGMENT_ID = RECORD_ID_PREFIX + res.data.id;
            records.find(RECORD_FRAGMENT_ID).replaceWith(recordsHtml.find(RECORD_FRAGMENT_ID));
        }
    });
});

/*
    Delete record
*/

const deleteRecordDialogModal = $(DELETE_RECORD_DIALOG_MODAL_ID);
const deleteRecordForm = deleteRecordDialogModal.find(DELETE_RECORD_FORM_ID);
const deleteRecordMessage = deleteRecordDialogModal.find(DELETE_RECORD_MESSAGE_ID);

deleteRecordDialogModal.on('show.bs.modal', function (event) {
    // Button that triggered the modal
    const button = event.relatedTarget;

    // Extract info from data-bs-* attributes
    const text = button.getAttribute('data-bs-text');
    const url = button.getAttribute('data-bs-url');

    // Update the modal's content.
    deleteRecordForm.attr('action', url);
    deleteRecordMessage.text(text);
});

setupAjaxFormSubmit(deleteRecordForm, function (res) {
    const RECORD_FRAGMENT_ID = RECORD_ID_PREFIX + res.data.id;
    const record = records.find(RECORD_FRAGMENT_ID);
    record.remove();
});
