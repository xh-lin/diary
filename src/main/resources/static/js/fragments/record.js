/*
    Variables passed from Thymeleaf in fragments/record::scripts:

    const RECORDS_FRAGMENT_URL;
*/
console.assert(RECORDS_FRAGMENT_URL !== undefined);

const RECORD_ID_PREFIX = '#record_';
const RECORDS_ID = '#records';
const LOAD_RECORDS_FORM_ID = '#loadRecordsForm'
const CREATE_RECORD_FORM_ID = '#createRecordForm';
const UPDATE_RECORD_DIALOG_MODAL_ID = '#updateRecordDialogModal';
const UPDATE_RECORD_FORM_ID = '#updateRecordForm';
const DELETE_RECORD_DIALOG_MODAL_ID = '#deleteRecordDialogModal';
const DELETE_RECORD_FORM_ID = '#deleteRecordForm';
const DELETE_RECORD_TEXT_ID = '#deleteRecordText';

const records = $(RECORDS_ID);
const loadRecordsForm = $(LOAD_RECORDS_FORM_ID);
const createRecordForm = $(CREATE_RECORD_FORM_ID);
const updateRecordDialogModal = $(UPDATE_RECORD_DIALOG_MODAL_ID);
const updateRecordForm = updateRecordDialogModal.find(UPDATE_RECORD_FORM_ID);
const deleteRecordDialogModal = $(DELETE_RECORD_DIALOG_MODAL_ID);
const deleteRecordForm = deleteRecordDialogModal.find(DELETE_RECORD_FORM_ID);

/*
    Convert records timestamps to local timezone
*/

formatTimestamps(records);

/*
    Load more records
*/

setupAjaxFormSubmit(loadRecordsForm, function (res) {
    // load records fragment
    $.ajax({
        type: 'POST',
        url: RECORDS_FRAGMENT_URL,
        data: JSON.stringify(res.data.content),
        contentType: 'application/json',
        error: errorHandler,
        success: function (recordsFragment) {
            const newRecords = $(recordsFragment);
            formatTimestamps(newRecords);
            records.append(newRecords.children());

            loadRecordsForm.attr('action', res.data.nextPageUrl);
            if (loadRecordsForm.attr('action') === undefined) {
                loadRecordsForm.hide();
            }
        }
    });
});

loadRecordsForm.find('button:submit').on('mouseup mouseout', function () {
    $(this).blur();
});

/*
    Create record
*/

setupAjaxFormSubmit(createRecordForm, function (res) {
    // load records fragment
    $.ajax({
        type: 'POST',
        url: RECORDS_FRAGMENT_URL,
        data: JSON.stringify([res.data]),
        contentType: 'application/json',
        error: errorHandler,
        success: function (recordsFragment) {
            const newRecords = $(recordsFragment);
            formatTimestamps(newRecords);
            records.prepend(newRecords.children());
        }
    });
});

/*
    Update record
*/

updateRecordDialogModal.on('show.bs.modal', function (event) {
    // Button that triggered the modal
    const button = event.relatedTarget;

    // Extract info from data-bs-* attributes
    const text = button.getAttribute('data-bs-text');
    const url = button.getAttribute('data-bs-url');

    // Update the modal's content.
    updateRecordForm.attr('action', url);
    updateRecordForm.find('textarea').val(text);
});

setupAjaxFormSubmit(updateRecordForm, function (res) {
    // load records fragment
    $.ajax({
        type: 'POST',
        url: RECORDS_FRAGMENT_URL,
        data: JSON.stringify([res.data]),
        contentType: 'application/json',
        error: errorHandler,
        success: function (recordsFragment) {
            const newRecords = $(recordsFragment);
            formatTimestamps(newRecords);
            // replace record fragment
            const RECORD_FRAGMENT_ID = RECORD_ID_PREFIX + res.data.id;
            records.find(RECORD_FRAGMENT_ID).replaceWith(newRecords.find(RECORD_FRAGMENT_ID));
        }
    });
});

/*
    Delete record
*/

deleteRecordDialogModal.on('show.bs.modal', function (event) {
    // Button that triggered the modal
    const button = event.relatedTarget;

    // Extract info from data-bs-* attributes
    const text = button.getAttribute('data-bs-text');
    const url = button.getAttribute('data-bs-url');

    // Update the modal's content.
    deleteRecordForm.attr('action', url);
    deleteRecordDialogModal.find(DELETE_RECORD_TEXT_ID).text(text);
});

setupAjaxFormSubmit(deleteRecordForm, function (res) {
    const RECORD_FRAGMENT_ID = RECORD_ID_PREFIX + res.data.id;
    const record = records.find(RECORD_FRAGMENT_ID);
    record.remove();
});
