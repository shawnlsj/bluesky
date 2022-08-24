class PasswordValidator {
    static passwordForm = document.getElementById("password");
    static passwordCheckForm = document.getElementById("passwordCheck");
    static passwordInvalidFeedback = document.getElementById("passwordInvalidFeedback");
    static passwordCheckInvalidFeedback = document.getElementById("passwordCheckInvalidFeedback");

    static isContainNotAllowedChar(password) {
        return /[^a-zA-Z0-9!"#$%&'()*+,\-.\/:;<=>?@\[\\\]^_`{|}~]/.test(password);
    }

    static isContainOnlyNumber(password) {
        return /^[0-9]+$/.test(password);
    }

    static isContainOnlyAlphabet(password) {
        return /^[a-zA-Z]+$/.test(password);
    }

    static isContainOnlySpecialChar(password) {
        return /^[!"#$%&'()*+,\-.\/:;<=>?@\[\\\]^_`{|}~]+$/.test(password);
    }

    static initEventListener() {
        const passwordForm = this.passwordForm;
        const passwordInvalidFeedback = this.passwordInvalidFeedback;
        const passwordCheckForm = this.passwordCheckForm;
        const passwordCheckInvalidFeedback = this.passwordCheckInvalidFeedback;

        if (passwordForm === null) {
            return;
        }

        passwordForm.addEventListener("keyup", function (event) {
            const password = passwordForm.value;
            let message = "";

            if (password === "" && event.key === "Enter") {
                return;
            }

            if (password === "") {
                disableValid(passwordForm);
                disableInvalid(passwordForm);
                return;
            }

            if (PasswordValidator.isContainNotAllowedChar(password)) {
                message = "사용할 수 없는 문자가 포함되어 있습니다.";

            } else if (password.length < MIN_PASSWORD_LENGTH) {
                message = MIN_PASSWORD_LENGTH + "자 이상 입력해 주세요.";

            } else if (password.length > MAX_PASSWORD_LENGTH) {
                message = MAX_PASSWORD_LENGTH + "자를 초과하여 입력할 수 없습니다.";

            } else if (PasswordValidator.isContainOnlyAlphabet(password)) {
                message = "숫자 또는 특수문자를 추가로 포함하여 주세요.";

            } else if (PasswordValidator.isContainOnlyNumber(password)) {
                message = "영문 또는 특수문자를 추가로 포함하여 주세요.";
            } else if (PasswordValidator.isContainOnlySpecialChar(password)) {
                message = "영문 또는 숫자를 추가로 포함하여 주세요.";
            } else {
                enableValid(passwordForm);
                return;
            }
            passwordInvalidFeedback.innerText = message;
            enableInvalid(passwordForm);
        });

        passwordCheckForm.addEventListener("keyup", function (event) {
            if ((passwordCheckForm.value === "" && event.key === "Enter")) {
                return;
            }

            if (passwordCheckForm.value === "") {
                disableValid(passwordCheckForm);
                disableInvalid(passwordCheckForm);
                return;
            }

            if (passwordForm.value === passwordCheckForm.value) {
                enableValid(passwordCheckForm);
            } else {
                passwordCheckInvalidFeedback.innerText = "비밀번호가 일치하지 않습니다.";
                enableInvalid(passwordCheckForm);
            }
        });
    }
}

export {PasswordValidator};
