/*
    Variables passed from Thymeleaf in fragments/tag::scripts:

    const TAG_BUTTONS_FRAGMENT_URL;
    const TAG_BADGES_FRAGMENT_URL;
*/
console.assert(TAG_BUTTONS_FRAGMENT_URL !== undefined);
console.assert(TAG_BADGES_FRAGMENT_URL !== undefined)

const TAG_BADGE_ID_PREFIX = '#tagBadge_';
const TAG_BUTTON_ID_PREFIX = '#tagButton_';
const INACTIVE_TAG_CLASS = 'inactive-tag';

const TAG_DIALOG_MODAL_ID = '#tagDialogModal';
const TAG_BADGES_ID = '#tagBadges';
const SELECT_TAG_FORM_ID = '#selectTagForm'
const TAG_BUTTONS_ID = '#tagButtons';

const CREATE_TAG_BUTTON_GROUP_ID = '#createTagButtonGroup';
const CREATE_TAG_FORM_TOGGLE_BUTTON_ID = '#createTagFormDisplayButton';
const CREATE_TAG_FORM_ID = '#createTagForm';

const UPDATE_TAG_FORM_ID = '#updateTagForm';

const DELETE_TAG_DIALOG_MODAL_ID = '#deleteTagDialogModal';
const DELETE_TAG_FORM_ID = '#deleteTagForm';
const DELETE_TAG_NAME_ID = '#deleteTagName';
const DELETE_TAG_RECORDS_SIZE_ID = '#deleteTagRecordsSize';

const TAG_DIALOG_MODAL_LABEL_ID = '#tagDialogModalLabel';
const SELECT_TAG_LABEL_ID = '#selectTagLabel';
const UPDATE_TAG_LABEL_ID = '#updateTagLabel';
const DELETE_TAG_LABEL_ID = '#deleteTagLabel';

const TAG_CANCEL_ACTION_BUTTON_ID = '#tagCancelActionButton';
const TAG_ACTION_BUTTONS_ID = '#tagActionButtons';
const UPDATE_TAG_BUTTON_ID = '#updateTagButton';
const DELETE_TAG_BUTTON_ID = '#deleteTagButton';

const tagDialogModal = $(TAG_DIALOG_MODAL_ID);
const selectTagForm = $(SELECT_TAG_FORM_ID);
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
            const id = tagButton.attr('data-bs-id');
            const actionPrefix = selectTagForm.attr('data-bs-action-prefix');
            selectTagForm.attr('action', `${actionPrefix}/${id}`);
            selectTagForm.attr('method', tagButton.parent().hasClass(INACTIVE_TAG_CLASS) ? 'PUT' : 'DELETE');
            selectTagForm.submit();
            break;
        case TagAction.UPDATE:
            updateTagForm.attr('action', url);
            updateTagForm.find('input[name=name]').val(name);
            showUpdateTagForm(tagButton);
            break;
        case TagAction.DELETE:
            const recordsSize = tagButton.attr('data-bs-records-size');
            deleteTagForm.attr('action', url);
            deleteTagForm.find(DELETE_TAG_NAME_ID).text(name);
            deleteTagForm.find(DELETE_TAG_NAME_ID).attr('title', name);
            deleteTagForm.find(DELETE_TAG_RECORDS_SIZE_ID).text(recordsSize);
            deleteTagDialogModal.show()
            break;
        default:
            console.error(`Unhandled TagAction: ${currentAction}`);
    }
}

tagButtons.find('button').click(tagOnClickHandler);

/*
    Select tag
*/

tagDialogModal.on('show.bs.modal', function (event) {
    // Button that triggered the modal
    const button = $(event.relatedTarget);

    // Extract info from data-bs-* attributes
    const url = button.attr('data-bs-url');

    // Update the modal's content.
    selectTagForm.attr('data-bs-action-prefix', url);

    // get active tag ids
    const tagBadges = button.closest('.card-footer').find(TAG_BADGES_ID);
    const activeTagButtonIdSet = new Set();
    tagBadges.children().each(function () {
        const tagId = $(this).attr('id').replace(TAG_BADGE_ID_PREFIX.substring(1), '');
        activeTagButtonIdSet.add(TAG_BUTTON_ID_PREFIX.substring(1) + tagId);
    })

    // set tag buttons to be active or inactive
    tagButtons.find(`[id^=${TAG_BUTTON_ID_PREFIX.substring(1)}]`).each(function () {
        if (activeTagButtonIdSet.has($(this).attr('id')))
            $(this).removeClass(INACTIVE_TAG_CLASS);
        else
            $(this).addClass(INACTIVE_TAG_CLASS);
    });
});

setupAjaxFormSubmit(selectTagForm, function (res) {
    const RECORD_FRAGMENT_ID = RECORD_ID_PREFIX + res.data.record.id; // RECORD_ID_PREFIX declared in record.js
    const TAG_BUTTON_FRAGMENT_ID = TAG_BUTTON_ID_PREFIX + res.data.tag.id;
    const tagBadges = $(RECORD_FRAGMENT_ID).find(TAG_BADGES_ID);
    const tagButton = tagButtons.find(TAG_BUTTON_FRAGMENT_ID);
    const recordsSize = parseInt(tagButton.find('button').attr('data-bs-records-size'));

    switch (selectTagForm.attr('method')) {
        case 'DELETE':
            // update tag button to inactive
            tagButton.addClass(INACTIVE_TAG_CLASS);
            tagButton.find('button').attr('data-bs-records-size', recordsSize - 1);
            // record fragment remove tag badge
            const TAG_BADGES_FRAGMENT_ID = TAG_BADGE_ID_PREFIX + res.data.tag.id;
            const tagBadge = tagBadges.find(TAG_BADGES_FRAGMENT_ID);
            tagBadge.remove();
            break;
        case 'PUT':
            // update tag button to active
            tagButton.removeClass(INACTIVE_TAG_CLASS);
            tagButton.find('button').attr('data-bs-records-size', recordsSize + 1);
            // load tag badges fragment
            $.ajax({
                type: 'POST',
                url: TAG_BADGES_FRAGMENT_URL,
                data: JSON.stringify([res.data.tag]),
                contentType: 'application/json',
                error: errorHandler,
                success: function (tagBadgesFragment) {
                    // record fragment add tag badge
                    const newTagBadges = $(tagBadgesFragment);
                    tagBadges.append(newTagBadges.children());
                }
            });
            break;
        default:
            console.error(`Unexpected method: ${method}`);
    }
}, false);

/*
    Create tag
*/

setupAjaxFormSubmit(createTagForm, function (res) {
    hideCreateTagForm();
    // load tag buttons fragment
    $.ajax({
        type: 'POST',
        url: TAG_BUTTONS_FRAGMENT_URL,
        data: JSON.stringify([res.data]),
        contentType: 'application/json',
        error: errorHandler,
        success: function (tagButtonsFragment) {
            const newTagButtons = $(tagButtonsFragment);
            // set to inactive
            newTagButtons.children().addClass(INACTIVE_TAG_CLASS);
            // setup on click handler
            newTagButtons.find('button').click(tagOnClickHandler);
            // append new tags fragment
            tagButtons.append(newTagButtons.children());
        }
    });
}, false);

/*
    Update tag
*/

setupAjaxFormSubmit(updateTagForm, function (res) {
    hideUpdateTagForm();
    // load tag buttons fragment
    $.ajax({
        type: 'POST',
        url: TAG_BUTTONS_FRAGMENT_URL,
        data: JSON.stringify([res.data]),
        contentType: 'application/json',
        error: errorHandler,
        success: function (tagButtonsFragment) {
            const newTagButtons = $(tagButtonsFragment);
            // setup on click handler
            newTagButtons.find('button').click(tagOnClickHandler);
            // replace new tag fragment
            const TAG_BUTTON_FRAGMENT_ID = TAG_BUTTON_ID_PREFIX + res.data.id;
            tagButtons.find(TAG_BUTTON_FRAGMENT_ID).html(newTagButtons.find(TAG_BUTTON_FRAGMENT_ID).children());
        }
    });

    // load tag badges fragment
    $.ajax({
        type: 'POST',
        url: TAG_BADGES_FRAGMENT_URL,
        data: JSON.stringify([res.data]),
        contentType: 'application/json',
        error: errorHandler,
        success: function (tagBadgesFragment) {
            // update record fragments' tag badge
            const TAG_BADGES_FRAGMENT_ID = TAG_BADGE_ID_PREFIX + res.data.id;
            $('.card-footer').each(function () {
                $(this).find(TAG_BADGES_FRAGMENT_ID).replaceWith($(tagBadgesFragment).find(TAG_BADGES_FRAGMENT_ID));
            });
        }
    });
}, false);

/*
    Delete tag
*/

setupAjaxFormSubmit(deleteTagForm, function (res) {
    const TAG_BUTTON_FRAGMENT_ID = TAG_BUTTON_ID_PREFIX + res.data.id;
    const tagButton = tagButtons.find(TAG_BUTTON_FRAGMENT_ID);
    tagButton.remove();

    // remove record fragments' tag badge
    const TAG_BADGES_FRAGMENT_ID = TAG_BADGE_ID_PREFIX + res.data.id;
    $('.card-footer').each(function () {
        $(this).find(TAG_BADGES_FRAGMENT_ID).remove();
    });
});
