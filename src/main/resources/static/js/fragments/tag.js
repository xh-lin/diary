const CREATE_TAG_FORM_TOGGLE_BUTTON_ID = '#createTagFormToggleButton';
const CREATE_TAG_FORM_ID = '#createTagForm';
const TAG_DIALOG_MODAL = '#tagDialogModal';

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
    // TODO: update page
}, false);
