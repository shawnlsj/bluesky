import {showFailModal} from "/js/util/modal.js";
import {EmailValidator} from "/js/validation/email_validator.js";

(function execute(){
    const form = document.getElementById("loginForm")
    const emailForm = EmailValidator.emailForm;

    EmailValidator.initEventListener();

    //로그인에 실패하여 이메일 폼에 값이 남아있는 경우에는 failModal 을 띄움
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
