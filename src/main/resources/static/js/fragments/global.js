/*
    get URL parameter
*/

function getUrlParam(name) {
    if (name = (new RegExp('[?&]'+encodeURIComponent(name)+'=([^&]*)')).exec(location.search))
        return decodeURIComponent(name[1]);
}

/*
    Redirect page
*/

function redirect(url, params) {
    $.each(params, function(k, v) {
        url += `?${k}=${v}`;
    });
    window.location.href = url;
}

/*
    Toast
*/

const TOAST_FRAGMENT_NOT_FOUUND = 'Toast fragment not found.';
const TOAST_ID = '#toast';
const TOAST_DELAY = 2000; // milliseconds

const toastFragment = $(TOAST_ID);
const toast = (toastFragment === undefined) ? undefined : new bootstrap.Toast(toastFragment, {delay: TOAST_DELAY});
const toastBody = (toastFragment === undefined) ? undefined : toastFragment.find('.toast-body');

function showToast(message) {
    if (toast === undefined) {
        alert(TOAST_FRAGMENT_NOT_FOUUND);
    } else {
        toastBody.html(message);
        toast.show();
    }
}

/*
    Use AJAX to submit a form
*/

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

                // success message
                showToast(res.message);

                // reset form and close dialog
                form[0].reset();
                form.closest('.modal').modal('hide');

                // update page
                successHandler(res);
            }
        });
    });
}

/*
    Error handler
*/

function errorHandler(jqXHR, textStatus, errorThrown) {
    console.error({
        jqXHR: jqXHR,
        textStatus: textStatus,
        errorThrown: errorThrown
    });
    const errorMessage = (jqXHR.responseJSON !== undefined && jqXHR.responseJSON.error !== undefined)
        ? jqXHR.responseJSON.error : jqXHR.statusText;
    alert(`Error - ${jqXHR.status}: ${errorMessage}`);
}

/*
    Convert and format timestamps to local timezone
*/

function convertTimestampsToLocalTimezone(parent, selector = '.timestamp') {
    parent.find(selector).each(function() {
        const date = new Date($(this).text());
        $(this).text(date.toLocaleDateString() + ' ' + date.toLocaleTimeString());
    });
}
