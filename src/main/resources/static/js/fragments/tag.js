/*
    Variables passed from Thymeleaf in fragments/tag::scripts:

    const TAGS_FRAGMENT_URL;
*/
console.assert(TAGS_FRAGMENT_URL !== undefined);

const TAG_ID_PREFIX = '#tag_';
const TAG_DIALOG_MODAL_ID = '#tagDialogModal';
const TAG_BUTTONS_ID = '#tagButtons';

const CREATE_TAG_BUTTON_GROUP_ID = '#createTagButtonGroup';
const CREATE_TAG_FORM_TOGGLE_BUTTON_ID = '#createTagFormToggleButton';
const CREATE_TAG_FORM_ID = '#createTagForm';

const DELETE_TAG_DIALOG_MODAL_ID = '#deleteTagDialogModal';
const DELETE_TAG_FORM_ID = '#deleteTagForm';
const DELETE_TAG_NAME_ID = '#deleteTagName';

const TAG_DIALOG_MODAL_LABEL_ID = '#tagDialogModalLabel';
const SELECT_TAG_LABEL_ID = '#selectTagLabel';
const DELETE_TAG_LABEL_ID = '#deleteTagLabel';

const TAG_CANCEL_ACTION_BUTTON_ID = '#tagCancelActionButton';
const TAG_ACTION_BUTTONS_ID = '#tagActionButtons';
const DELETE_TAG_BUTTON_ID = '#deleteTagButton';

const tagDialogModal = $(TAG_DIALOG_MODAL_ID);
const tagButtons = tagDialogModal.find(TAG_BUTTONS_ID);

const createTagButtonGroup = $(CREATE_TAG_BUTTON_GROUP_ID);
const createTagFormToggleButton = createTagButtonGroup.find(CREATE_TAG_FORM_TOGGLE_BUTTON_ID);
const createTagForm = createTagButtonGroup.find(CREATE_TAG_FORM_ID);

const deleteTagDialogModal = new bootstrap.Modal(DELETE_TAG_DIALOG_MODAL_ID);
const deleteTagForm = $(DELETE_TAG_DIALOG_MODAL_ID).find(DELETE_TAG_FORM_ID);

const tagDialogModalLabel = $(TAG_DIALOG_MODAL_LABEL_ID);
const selectTagLabel = tagDialogModalLabel.find(SELECT_TAG_LABEL_ID);
const deleteTagLabel = tagDialogModalLabel.find(DELETE_TAG_LABEL_ID);

const tagCancelActionButton = $(TAG_CANCEL_ACTION_BUTTON_ID);
const tagActionButtons = $(TAG_ACTION_BUTTONS_ID);
const deleteTagButtons = tagActionButtons.find(DELETE_TAG_BUTTON_ID);

// enum for tag action states
const TagAction = {
    SELECT: 'SELECT',
    DELETE: 'DELETE'
};
Object.freeze(TagAction);
let currentAction = TagAction.SELECT;

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
    tagDialogModalLabel.children().hide()
    selectTagLabel.show();

    // change action button
    tagCancelActionButton.hide();
    tagActionButtons.show();

    // Show create tag action button
    createTagButtonGroup.show();
    hideCreateTagForm();
}

function showDeleteTagAction() {
    currentAction = TagAction.DELETE;

    // change dialog title
    tagDialogModalLabel.children().hide()
    deleteTagLabel.show();

    // change action button
    tagActionButtons.hide();
    tagCancelActionButton.show();

    // Hide create tag action button
    createTagButtonGroup.hide();
}

/*
    Toggle create tag form display button
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
    Create tag form submit
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
            const newTags = $(tagsFragment);
            // setup on click handler
            newTags.find('button').click(tagOnClickHandler);
            // append new tags fragment
            tagButtons.append(newTags.children());
        }
    });
}, false);

/*
    Toggle delete tag action button
*/

deleteTagButtons.click(function () {
    showDeleteTagAction();
});

/*
    Cancel tag action button
*/

tagCancelActionButton.click(function () {
    showSelectTagAction();
});

/*
    Tag button on click handler
*/

function tagOnClickHandler(event) {
    switch (currentAction) {
        case TagAction.SELECT:
            break;
        case TagAction.DELETE:
            const button = $(event.currentTarget);

            // Extract info from data-bs-* attributes
            const name = button.attr('data-bs-name');
            const url = button.attr('data-bs-url');

            // Update the modal's content.
            deleteTagForm.attr('action', url);
            deleteTagForm.find(DELETE_TAG_NAME_ID).text(name);

            deleteTagDialogModal.show()
            break;
        default:
            console.error(`Unhandled TagAction: ${currentAction}`);
    }
}

tagButtons.find('button').click(tagOnClickHandler);

/*
    Delete tag form submit
*/

setupAjaxFormSubmit(deleteTagForm, function (res) {
    const TAG_FRAGMENT_ID = TAG_ID_PREFIX + res.data.id;
    const tagButton = tagButtons.find(TAG_FRAGMENT_ID);
    tagButton.remove();
});