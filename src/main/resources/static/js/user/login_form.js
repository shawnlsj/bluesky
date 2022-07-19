import {showFailModal} from "/js/util/modal.js";
import {EmailValidator} from "/js/validation/email_validator.js";

(function execute(){
    const form = document.getElementById("loginForm")
    const emailForm = EmailValidator.emailForm;

    EmailValidator.initEventListener();

    if (emailForm.value !== "") {
        showFailModal();
    }

    form.addEventListener("submit", function (event) {
        event.preventDefault();
        if (isInvalidStatus(emailForm)) {
            emailForm.focus();
            return;
        }
        form.submit();
    });
})();
