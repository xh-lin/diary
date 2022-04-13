/*
    Variables passed from Thymeleaf in fragments/tag::scripts:

    const TAGS_FRAGMENT_URL;
*/
console.assert(TAGS_FRAGMENT_URL !== undefined);

const TAG_ID_PREFIX = '#tag_';
const TAG_DIALOG_MODAL_ID = '#tagDialogModal';
const TAG_BUTTONS_ID = '#tagButtons';

const CREATE_TAG_BUTTON_GROUP_ID = '#createTagButtonGroup';
const CREATE_TAG_FORM_TOGGLE_BUTTON_ID = '#createTagFormDisplayButton';
const CREATE_TAG_FORM_ID = '#createTagForm';

const UPDATE_TAG_FORM_ID = '#updateTagForm';

const DELETE_TAG_DIALOG_MODAL_ID = '#deleteTagDialogModal';
const DELETE_TAG_FORM_ID = '#deleteTagForm';
const DELETE_TAG_NAME_ID = '#deleteTagName';

const TAG_DIALOG_MODAL_LABEL_ID = '#tagDialogModalLabel';
const SELECT_TAG_LABEL_ID = '#selectTagLabel';
const UPDATE_TAG_LABEL_ID = '#updateTagLabel';
const DELETE_TAG_LABEL_ID = '#deleteTagLabel';

const TAG_CANCEL_ACTION_BUTTON_ID = '#tagCancelActionButton';
const TAG_ACTION_BUTTONS_ID = '#tagActionButtons';
const UPDATE_TAG_BUTTON_ID = '#updateTagButton';
const DELETE_TAG_BUTTON_ID = '#deleteTagButton';

const tagDialogModal = $(TAG_DIALOG_MODAL_ID);
const tagButtons = tagDialogModal.find(TAG_BUTTONS_ID);

const createTagButtonGroup = $(CREATE_TAG_BUTTON_GROUP_ID);
const createTagFormDisplayButton = createTagButtonGroup.find(CREATE_TAG_FORM_TOGGLE_BUTTON_ID);
const createTagForm = createTagButtonGroup.find(CREATE_TAG_FORM_ID);

const updateTagForm = $(UPDATE_TAG_FORM_ID);

const deleteTagDialogModal = new bootstrap.Modal(DELETE_TAG_DIALOG_MODAL_ID);
const deleteTagForm = $(DELETE_TAG_DIALOG_MODAL_ID).find(DELETE_TAG_FORM_ID);

const tagDialogModalLabel = $(TAG_DIALOG_MODAL_LABEL_ID);
const selectTagLabel = tagDialogModalLabel.find(SELECT_TAG_LABEL_ID);
const updateTagLabel = tagDialogModalLabel.find(UPDATE_TAG_LABEL_ID);
const deleteTagLabel = tagDialogModalLabel.find(DELETE_TAG_LABEL_ID);

const tagCancelActionButton = $(TAG_CANCEL_ACTION_BUTTON_ID);
const tagActionButtons = $(TAG_ACTION_BUTTONS_ID);
const updateTagButton = $(UPDATE_TAG_BUTTON_ID);
const deleteTagButton = tagActionButtons.find(DELETE_TAG_BUTTON_ID);

// enum for tag action states
const TagAction = {
    SELECT: 'SELECT',
    UPDATE: 'UPDATE',
    DELETE: 'DELETE'
};
Object.freeze(TagAction);
let currentAction = TagAction.SELECT;

/*
    Functions for toggling tag actions
*/

function showCreateTagForm() {
    createTagFormDisplayButton.addClass('d-none');
    createTagForm.removeClass('d-none')
}

function hideCreateTagForm() {
    createTagFormDisplayButton.removeClass('d-none')
    createTagForm.addClass('d-none');
}

function showUpdateTagForm(tagButton) {
    tagButtons.find('button.d-none').removeClass('d-none');
    updateTagForm.removeClass('d-none');
    tagButton.parent().after(updateTagForm);
    tagButton.addClass('d-none');
}

function hideUpdateTagForm() {
    tagButtons.find('button.d-none').removeClass('d-none');
    updateTagForm.addClass('d-none');
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
    hideUpdateTagForm();
}

function showUpdateTagAction() {
    currentAction = TagAction.UPDATE;
    // change dialog title
    tagDialogModalLabel.children().hide()
    updateTagLabel.show();
    // change action button
    tagActionButtons.hide();
    tagCancelActionButton.show();
    // Hide create tag action button
    createTagButtonGroup.hide();
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
    Toggle tag actions
*/

createTagFormDisplayButton.click(function () {
    showCreateTagForm();
});

updateTagButton.click(function () {
    showUpdateTagAction();
});

deleteTagButton.click(function () {
    showDeleteTagAction();
});

tagCancelActionButton.click(function () {
    showSelectTagAction();
});

tagDialogModal.on('hidden.bs.modal', function () {
    showSelectTagAction();
});

/*
    Tag button on click handler
*/

function tagOnClickHandler(event) {
    const tagButton = $(event.currentTarget);
    // Extract info from data-bs-* attributes
    const name = tagButton.attr('data-bs-name');
    const url = tagButton.attr('data-bs-url');
    switch (currentAction) {
        case TagAction.SELECT:
            // TODO
            break;
        case TagAction.UPDATE:
            // Update the modal's content.
            updateTagForm.attr('action', url);
            updateTagForm.find('input[name=name]').val(name);
            showUpdateTagForm(tagButton);
            break;
        case TagAction.DELETE:
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
    Update tag form submit
*/

setupAjaxFormSubmit(updateTagForm, function (res) {
    hideUpdateTagForm();
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
            // replace new tag fragment
            const TAG_FRAGMENT_ID = TAG_ID_PREFIX + res.data.id;
            tagButtons.find(TAG_FRAGMENT_ID).replaceWith(newTags.find(TAG_FRAGMENT_ID));
        }
    });
}, false);

/*
    Delete tag form submit
*/

setupAjaxFormSubmit(deleteTagForm, function (res) {
    const TAG_FRAGMENT_ID = TAG_ID_PREFIX + res.data.id;
    const tagButton = tagButtons.find(TAG_FRAGMENT_ID);
    tagButton.remove();
});
