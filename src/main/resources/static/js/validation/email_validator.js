import {MAX_EMAIL_LENGTH} from "/js/config/user_option.js";

class EmailValidator {
    static emailForm = document.getElementById("email");

    static isValid(email){
        const EMAIL_VALIDATION_REGEX = /^[^@]+@[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])*(\.[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])*)*(\.[a-zA-Z][a-zA-Z0-9-]*[a-zA-Z0-9])$/;
        return email.match(EMAIL_VALIDATION_REGEX) && email.length <= MAX_EMAIL_LENGTH;
    }

    static initEventListener() {
        const emailForm = this.emailForm;

        if (emailForm === null) {
            return;
        }

        emailForm.addEventListener("keyup", function () {
            const email = emailForm.value;
            if (EmailValidator.isValid(email)) {
                enableValid(emailForm);
            } else {
                enableInvalid(emailForm);
            }
        });
    }
}

export {EmailValidator};