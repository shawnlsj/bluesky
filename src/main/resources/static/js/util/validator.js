const MAX_EMAIL_LENGTH = 50;
const EMAIL_VALIDATION_REGEX = /^[^@]+@[a-zA-Z0-9]+[a-zA-Z0-9-]*[a-zA-Z0-9](\.[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9])*(\.[a-zA-Z][a-zA-Z0-9-]*[a-zA-Z0-9])$/;
export const emailForm = document.getElementById("email");

export function initEmailFormEventListener() {
    emailForm.addEventListener("keyup", function () {
        const email = emailForm.value;
        if (email.match(EMAIL_VALIDATION_REGEX) && email.length <= MAX_EMAIL_LENGTH) {
            enableValid(emailForm);
        } else {
            enableInvalid(emailForm);
        }
    });
}