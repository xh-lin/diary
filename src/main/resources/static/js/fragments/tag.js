/*
    Variables passed from Thymeleaf in fragments/tag::scripts:

    const TAGS_FRAGMENT_URL;
*/
console.assert(TAGS_FRAGMENT_URL !== undefined);

const TAG_ID_PREFIX = '#tag_';
const TAG_BUTTONS_ID = '#tagButtons';
const TAG_DIALOG_MODAL_ID = '#tagDialogModal';

const CREATE_TAG_BUTTON_GROUP_ID = '#createTagButtonGroup';
const CREATE_TAG_FORM_TOGGLE_BUTTON_ID = '#createTagFormToggleButton';
const CREATE_TAG_FORM_ID = '#createTagForm';

const TAG_DIALOG_MODAL_LABEL_ID = '#tagDialogModalLabel';
const SELECT_TAG_LABEL_ID = '#selectTagLabel';
const DELETE_TAG_LABEL_ID = '#deleteTagLabel';

const TAG_ACTION_BUTTONS_ID = '#tagActionButtons';
const TAG_CANCEL_ACTION_BUTTON_ID = '#tagCancelActionButton';

// enum for tag action states
const TagAction = {
    SELECT: 'SELECT',
    DELETE: 'DELETE'
};
Object.freeze(TagAction);
let currentAction = TagAction.SELECT;

const tagDialogModal = $(TAG_DIALOG_MODAL_ID);
const tagButtons = $(TAG_BUTTONS_ID);
const createTagFormToggleButton = $(CREATE_TAG_FORM_TOGGLE_BUTTON_ID);
const createTagForm = $(CREATE_TAG_FORM_ID);

/*
    Functions for toggling tag actions
*/

function showCreateTagForm() {
    createTagFormToggleButton.addClass('d-none');
    createTagForm.removeClass('d-none')
}

function hideCreateTagForm() {
    createTagFormToggleButton.removeClass('d-none')
    createTagForm.addClass('d-none');
}

function showSelectTagAction() {
    currentAction = TagAction.SELECT;

    // change dialog title
    tagDialogModal.find(TAG_DIALOG_MODAL_LABEL_ID).children().hide()
    tagDialogModal.find(SELECT_TAG_LABEL_ID).show();

    // change action button
    tagDialogModal.find(TAG_CANCEL_ACTION_BUTTON_ID).hide();
    tagDialogModal.find(TAG_ACTION_BUTTONS_ID).show();

    // Show create tag action button
    tagDialogModal.find(CREATE_TAG_BUTTON_GROUP_ID).show();
    hideCreateTagForm();
}

function showDeleteTagAction() {
    currentAction = TagAction.DELETE;

    // change dialog title
    tagDialogModal.find(TAG_DIALOG_MODAL_LABEL_ID).children().hide()
    tagDialogModal.find(DELETE_TAG_LABEL_ID).show();

    // change action button
    tagDialogModal.find(TAG_ACTION_BUTTONS_ID).hide();
    tagDialogModal.find(TAG_CANCEL_ACTION_BUTTON_ID).show();

    // Hide create tag action button
    tagDialogModal.find(CREATE_TAG_BUTTON_GROUP_ID).hide();
}

/*
    Click button to show create tag form
*/

createTagFormToggleButton.click(function () {
    showCreateTagForm();
});

/*
    Reset action state after dialog closes
*/

tagDialogModal.on('hidden.bs.modal', function () {
    showSelectTagAction();
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

/*
    Delete tag
*/

tagDialogModal.find('#deleteTagButton').click(function () {
    showDeleteTagAction();
});

/*
    Button for canceling tag action
*/

tagDialogModal.find('#tagCancelActionButton').click(function () {
    showSelectTagAction();
});
