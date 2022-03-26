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