function checkAccessToken(){
    let token = localStorage.getItem('token');
    let expirationDate = getExpirationDate(token);
    return expirationDate > new Date();
}

function getExpirationDate(jwtToken) {
    let payload;
    let parts = jwtToken.split('.');
    if (parts.length === 3) {
        payload = parts[1];
        let decodedPayload = atob(payload);
        let jsonPayload = JSON.parse(decodedPayload);
        // Get the expiration timestamp
        let expTimestamp = jsonPayload.exp;
        // Convert the timestamp to a Date and return it
        return new Date(expTimestamp * 1000);
    } else {
        return null;
    }
}

function pagination(totalPage, currentPage) {
    window.pagObj = $('#pagination').twbsPagination({
        totalPages: totalPage,
        visiblePages: 5,
        startPage: currentPage,
        onPageClick: function (event, page) {
            if (page !== currentPage) {
                $('#page').val(page - 1);
                removeEmptyInputs('formSubmit');
                $('#formSubmit').submit();
            }
        }
    })
}

function removeEmptyInputs(formId) {
    $('#' + formId + ' input[type="hidden"]').each(function () {
        if ($(this).val() === '') {
            $(this).remove();
        }
    });
}
$(document).ready(function(){
    const slideInLeftKeyframes = `
                      @keyframes slideInLeft {
                        from {
                          opacity: 0;
                          transform: translateX(calc(100% + 32px));
                        }
                        to {
                          opacity: 1;
                          transform: translateX(0);
                        }
                      }
                      `;
    const fadeOutKeyframes = `
                      @keyframes fadeOut {
                         to {
                           opacity: 0;
                             }
                      }
                      `;
    const styleElement = $('<style>').attr('type', 'text/css').text(slideInLeftKeyframes + fadeOutKeyframes);
    $('head').append(styleElement);
});