import {showFailModal} from "/js/util/modal.js";
import {PasswordValidator} from "/js/validation/password_validator.js";
import {NicknameValidator} from "/js/validation/nickname_validator.js";

(function execute() {
    const form = document.getElementById("joinForm");
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

    //이메일 가입 회원인지 확인
    //패스워드 폼이 존재하면 이메일 가입 회원
    function isOriginalUser() {
        return PasswordValidator.passwordForm !== null;

    }

    if (isOriginalUser()) {
        PasswordValidator.initEventListener();
        NicknameValidator.initEventListener();
    }

    //submit 버튼을 클릭할 때는 blur 이벤트가 발행되지 않도록 한다
    sendButton.addEventListener("mousedown", function (event) {
        event.preventDefault();
    });

    form.addEventListener("submit", function (event) {
        event.preventDefault();

        const nicknameForm = NicknameValidator.nicknameForm;
        const nicknameInvalidFeedback = NicknameValidator.nicknameInvalidFeedback;

        const passwordForm = PasswordValidator.passwordForm;
        const passwordInvalidFeedback = PasswordValidator.passwordInvalidFeedback;
        const passwordCheckForm = PasswordValidator.passwordCheckForm;
        const passwordCheckInvalidFeedback = PasswordValidator.passwordCheckInvalidFeedback;

        //blur 이벤트로 유효성 검사를 하는 폼은 재검사를 위해 invalid 상태를 꺼둔다
        if (isInvalidStatus(nicknameForm)) {
            disableInvalid(nicknameForm);
        }

        let checkTargetArr = [];
        //비어있는 폼들이 있는지 확인
        if (nicknameForm != null) {
            checkTargetArr.push(nicknameForm);

            //닉네임 폼에 값이 있다면 유효성 검사를 실행
            if (!isEmpty(nicknameForm, nicknameInvalidFeedback)) {
                NicknameValidator.checkFormat(nicknameForm.value);
            }
        }
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

        const xhr = new XMLHttpRequest();
        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                switch (this.status) {
                    case 200:
                        const checkNicknameResult = JSON.parse(xhr.responseText);
                        if (checkNicknameResult.available) {
                            form.submit();
                        } else {
                            displayOff(sendButtonSpinner);
                            displayOn(sendButton);
                            const message = "'" + nicknameForm.value + "' 은(는) 이미 다른 사용자가 사용중입니다.";
                            nicknameInvalidFeedback.innerText = message;
                            enableInvalid(nicknameForm);
                            nicknameForm.focus();
                        }
                        return;
                    default:
                        displayOff(sendButtonSpinner);
                        displayOn(sendButton);
                        showFailModal();
                        return;
                }
            }
        });
        xhr.open("GET", "/user/availability?nickname=" + nicknameForm.value);
        xhr.send();
    });
})();
