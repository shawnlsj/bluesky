import {PasswordValidator} from "/js/validation/password_validator.js";

(function execute() {
    const form = document.getElementById("resetPasswordForm");
    const sendButton = document.getElementById("sendButton");
    const sendButtonSpinner = document.getElementById("sendButtonSpinner");

    function isInvalidForm(inputElement) {
        //input 이 invalid 상태라면 포커싱을 한다
        if (isInvalidStatus(inputElement)) {
            inputElement.focus();
            return true;
        }
        return false;
    }

    function isEmpty(inputElement, invalidFeedback) {
        if (inputElement.value === "") {
            enableInvalid(inputElement);
            invalidFeedback.innerText = "필수 입력 항목입니다.";
            return true;
        }
        return false;
    }

    PasswordValidator.initEventListener();

    form.addEventListener("submit", function (event) {
        event.preventDefault();

        const passwordForm = PasswordValidator.passwordForm;
        const passwordInvalidFeedback = PasswordValidator.passwordInvalidFeedback;
        const passwordCheckForm = PasswordValidator.passwordCheckForm;
        const passwordCheckInvalidFeedback = PasswordValidator.passwordCheckInvalidFeedback;

        let checkTargetArr = [];
        //비어있는 폼들이 있는지 확인
        if (passwordForm != null) {
            checkTargetArr.push(passwordForm);
            isEmpty(passwordForm, passwordInvalidFeedback);
        }
        if (passwordCheckForm != null) {
            checkTargetArr.push(passwordCheckForm);
            isEmpty(passwordCheckForm, passwordCheckInvalidFeedback);
        }

        //유효성 검사를 통과하지 못한 입력창이 있으면 포커싱
        const existsInvalidForm = checkTargetArr.some(isInvalidForm);
        if (existsInvalidForm) {
            return;
        }

        displayOff(sendButton);
        displayOn(sendButtonSpinner);
        form.submit();
    });
})();