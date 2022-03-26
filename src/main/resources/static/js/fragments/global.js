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

const TOAST_ID = '#toast';
const TOAST_DELAY = 2000; // milliseconds

const toast = new bootstrap.Toast($(TOAST_ID), {delay: TOAST_DELAY});
const toastBody = $(`${TOAST_ID} .toast-body`);

function showToast(message) {
    toastBody.html(message);
    toast.show();
}