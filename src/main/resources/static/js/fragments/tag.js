/*
    Variables passed from Thymeleaf in fragments/tag::scripts:

    const TAGS_FRAGMENT_URL;
*/
console.assert(TAGS_FRAGMENT_URL !== undefined);

const TAG_BUTTONS_ID = '#tagButtons';
const CREATE_TAG_FORM_TOGGLE_BUTTON_ID = '#createTagFormToggleButton';
const CREATE_TAG_FORM_ID = '#createTagForm';
const TAG_DIALOG_MODAL = '#tagDialogModal';

const tagButtons = $(TAG_BUTTONS_ID);
const createTagFormToggleButton = $(CREATE_TAG_FORM_TOGGLE_BUTTON_ID);
const createTagForm = $(CREATE_TAG_FORM_ID);
const tagDialogModal = $(TAG_DIALOG_MODAL);

/*
    Functions for toggling create tag form
*/

function showCreateTagForm() {
    createTagFormToggleButton.addClass('d-none');
    createTagForm.removeClass('d-none')
}

function hideCreateTagForm() {
    createTagFormToggleButton.removeClass('d-none')
    createTagForm.addClass('d-none');
}

/*
    Click button to show create tag form
*/

createTagFormToggleButton.click(function () {
    showCreateTagForm();
});

/*
    Hide create tag form after dialog closes
*/

tagDialogModal.on('hidden.bs.modal', function () {
    hideCreateTagForm();
});

/*
    create tag
*/

setupAjaxFormSubmit(createTagForm, function (res) {
    hideCreateTagForm();
    // load tags fragment
    $.ajax({
        type: 'POST',
        url: TAGS_FRAGMENT_URL,
        data: JSON.stringify([res.data]),
        contentType: 'application/json',
        error: errorHandler,
        success: function (tagsFragment) {
            // append new tags fragment
            tagButtons.append($(tagsFragment).children());
        }
    });
}, false);
