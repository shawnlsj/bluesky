const successModalElement = document.getElementById("successModal");
const successModalBody = document.getElementById("successModalBody");
const successModal = initSuccessModal();
const failModalElement = document.getElementById("failModal");
const failModalBody = document.getElementById("failModalBody");
const failModal = initFailModal();

function initSuccessModal() {
    if (successModalElement !== null) {
        return new bootstrap.Modal(successModalElement);
    }
    return null;
}

function initFailModal() {
    if (failModalElement !== null) {
        return new bootstrap.Modal(failModalElement);
    }
    return null;
}

(function init() {
    document.addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            if (successModal !== null) {
                successModal.hide();
            }
            if (failModal !== null) {
                failModal.hide();
            }
        }
    });
    if (successModalElement !== null) {
        successModalElement.addEventListener("shown.bs.modal", function () {
            document.activeElement.blur();
        });
    }
    if (failModalElement !== null) {
        failModalElement.addEventListener("shown.bs.modal", function () {
            document.activeElement.blur();
        });
    }
})();

export function showSuccessModal(message) {
    if (message !== undefined) {
        successModalBody.innerText = message;
    }
    successModal.show();
}

export function showFailModal(message) {
    if (message !== undefined) {
        failModalBody.innerText = message;
    }
    failModal.show();
}

export {successModal, successModalBody, failModal, failModalBody};
