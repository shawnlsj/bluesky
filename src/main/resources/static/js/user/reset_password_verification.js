import {showSuccessModal, showFailModal} from "/js/util/modal.js";
import {EmailValidator} from "/js/validation/email_validator.js";

(function execute(){
    const form = document.getElementById("verificationForm");
    const emailForm = EmailValidator.emailForm;
    const emailHiddenForm = document.getElementById("hiddenEmail");

    const sendButton = document.getElementById("sendButton");
    const sendSpinnerButton = document.getElementById("sendButtonSpinner");

    const resendMessage = document.getElementsByClassName("message-resend")[0];
    const resendButton = document.getElementById("resendButton");
    const resendSpinner = document.getElementById("resendSpinner");

    const INVALID_FORMAT_MESSAGE = "이메일 형식이 올바르지 않습니다.";
    const EMAIL_NOT_FOUND_MESSAGE = "등록된 이메일이 아닙니다.";

    function sendMail() {
        const formData = new FormData(form);
        const xhr = new XMLHttpRequest();

        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                const sendEmailResult = JSON.parse(xhr.responseText);
                disableValid(emailForm);

                if (sendEmailResult.success) {
                    emailForm.placeholder = emailForm.value;
                    emailForm.value = "";
                    emailForm.disabled = true;
                    sendButton.disabled = true;
                    sendButton.classList.remove("btn-primary");
                    sendButton.classList.add("btn-secondary");
                    displayOff(sendSpinnerButton);
                    displayOn(sendButton);
                    displayOn(resendMessage);
                    showSuccessModal();
                } else {
                    displayOff(sendSpinnerButton);
                    displayOn(sendButton);
                    showFailModal(EMAIL_NOT_FOUND_MESSAGE);
                }
            }
        });
        xhr.open("POST", "/reset-password/send-email");
        xhr.send(formData);
    }

    function resendMail() {
        const formData = new FormData(form);
        const xhr = new XMLHttpRequest();

        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                const sendEmailResult = JSON.parse(xhr.responseText);
                displayOff(resendSpinner);
                displayOn(resendButton);

                if (sendEmailResult.success) {
                    showSuccessModal();
                } else {
                    showFailModal(EMAIL_NOT_FOUND_MESSAGE);
                }
            }
        });
        xhr.open("POST", "/reset-password/send-email");
        xhr.send(formData);
    }

    EmailValidator.initEventListener();

    sendButton.addEventListener("click", function () {
        //유효성 검증이 통과되었을 때만 스피너 버튼을 보여준다.
        if (isValidStatus(emailForm)) {
            displayOff(sendButton);
            displayOn(sendSpinnerButton);
        }
    });

    resendButton.addEventListener("click", function (event) {
        //a 태그의 기본동작을 막는다.
        event.preventDefault();

        //resendButton 의 실제 계산된 넓이를 resendSpinner 에게 그대로 부여한다.
        if (resendSpinner.style.width === "") {
            resendSpinner.style.width = resendButton.getBoundingClientRect().width + "px";
        }
        displayOff(resendButton);
        displayOn(resendSpinner);
        resendMail();
    });

    form.addEventListener("submit", function (event) {
        event.preventDefault();

        if (emailForm.value === "") {
            enableInvalid(emailForm);
            emailForm.focus();
            return;
        } else if (isInvalidStatus(emailForm)) {
            showFailModal(INVALID_FORMAT_MESSAGE);
            return;
        }

        //이메일 입력창이 disabled 상태가 아닐 때만 hidden 필드로 값을 복사한다.
        if (emailForm.disabled === false) {
            emailHiddenForm.value = emailForm.value;
        }
        sendMail();
    });
})();
