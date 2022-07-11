import {hello2} from "/js/util/modal.js";
hello2();
window.onload = init;
const successModalElement = document.getElementById("successModal");
const failModalElement = document.getElementById("failModal");

const successModal = new bootstrap.Modal(successModalElement);
const failModal = new bootstrap.Modal(failModalElement)

const form = document.getElementById("verificationForm");
const emailForm = document.getElementById("email");
const emailHiddenForm = document.getElementById("hiddenEmail");
const sendButton = document.getElementById("sendButton");
const sendSpinnerButton = document.getElementById("sendButtonSpinner");
const resendMessage = document.getElementsByClassName("message-resend")[0];
const resendButton = document.getElementById("resendButton");
const resendSpinner = document.getElementById("resendSpinner");

const MAX_EMAIL_LENGTH = 50;
const EMAIL_VALIDATION_REGEX = /^[^@]+@[a-zA-Z0-9]+[a-zA-Z0-9-]*[a-zA-Z0-9](\.[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9])*(\.[a-zA-Z][a-zA-Z0-9-]*[a-zA-Z0-9])$/;

const INVALID_FORMAT_MESSAGE = "이메일 형식이 올바르지 않습니다.";
const DUPLICATE_EMAIL_MESSAGE = "이미 등록된 이메일입니다.";

function init() {
    initEventListeners();
}

function initEventListeners() {
    successModalElement.addEventListener("shown.bs.modal", function () {
        document.activeElement.blur();
    });
    failModalElement.addEventListener("shown.bs.modal", function () {
        document.activeElement.blur();
    });
    document.addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            successModal.hide();
            failModal.hide();
        }
    });

    emailForm.addEventListener("keyup", function () {
        const email = emailForm.value;
        if (email.match(EMAIL_VALIDATION_REGEX) && email.length <= MAX_EMAIL_LENGTH) {
            enableValid(emailForm);
        } else {
            enableInvalid(emailForm);
        }
    });

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

        if (isEmpty(emailForm)) {
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
}

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
                successModal.show();
            } else {
                displayOff(sendSpinnerButton);
                displayOn(sendButton);
                showFailModal(DUPLICATE_EMAIL_MESSAGE);
            }
        }
    });
    xhr.open("POST", "/join/send-email");
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
                successModal.show();
            } else {
                showFailModal(DUPLICATE_EMAIL_MESSAGE);
            }
        }
    });
    xhr.open("POST", "/join/send-email");
    xhr.send(formData);
}

function isEmpty(inputElement) {
    if (inputElement.value === "") {
        enableInvalid(inputElement);
        return true;
    }
    return false;
}

function showFailModal(message) {
    failModalMessage.innerText = message;
    failModal.show();
}
