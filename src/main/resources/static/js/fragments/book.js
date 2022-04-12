/*
    Variables passed from Thymeleaf in fragments/diary::scripts:

    const DIARY_URL;
    const BOOKS_FRAGMENT_URL;
    let currentBookId;
*/
console.assert(DIARY_URL !== undefined);
console.assert(BOOKS_FRAGMENT_URL !== undefined);
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

const bookLinks = $(BOOK_LINKS_ID);

/*
    Create Book
*/

const createBookForm = $(CREATE_BOOK_FORM_ID);

setupAjaxFormSubmit(createBookForm, function (res) {
    // load book fragment
    $.ajax({
        type: 'POST',
        url: BOOKS_FRAGMENT_URL,
        data: JSON.stringify([res.data]),
        contentType: 'application/json',
        error: errorHandler,
        success: function (booksFragment) {
            if (currentBookId === null) {
                redirect(DIARY_URL, { toast: res.message });
            } else {
                // append new book fragment
                bookLinks.append($(booksFragment).children());
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
        url: BOOKS_FRAGMENT_URL,
        data: JSON.stringify([res.data]),
        contentType: 'application/json',
        error: errorHandler,
        success: function (booksFragment) {
            // replace book fragment
            const BOOK_FRAGMENT_ID = BOOK_ID_PREFIX + res.data.id;
            bookLinks.find(BOOK_FRAGMENT_ID).replaceWith($(booksFragment).find(BOOK_FRAGMENT_ID));
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
    deleteBookMessage.text(title);
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
        bookLink.remove();
    }
});
