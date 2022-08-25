const successModalElement = document.getElementById("successModal");
const successModalBody = document.getElementById("successModalBody");
const successModal = initSuccessModal();

const failModalElement = document.getElementById("failModal");
const failModalTitle = document.getElementById("failModalTitle");
const failModalBody = document.getElementById("failModalBody");
const failModal = initFailModal();

const confirmModalElement = document.getElementById("confirmModal");
const confirmModalTitle = document.getElementById("confirmModalTitle");
const confirmModalBody = document.getElementById("confirmModalBody");
const confirmModalConfirmButton = document.getElementById("confirmModalConfirmButton");
const confirmModalCancelButton = document.getElementById("confirmModalCancelButton");

let confirmModal = initConfirmModal();

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

function initConfirmModal() {
    if (confirmModalElement !== null) {
        return new bootstrap.Modal(confirmModalElement);
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
            if (confirmModal !== null) {
                confirmModal.hide()
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
    if (confirmModalElement !== null) {
        confirmModalElement.addEventListener("shown.bs.modal", function () {
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

export function showFailModal(message, title) {
    if (message !== undefined) {
        failModalBody.innerText = message;
    }
    if (title !== undefined) {
        failModalTitle.innerText = title;
    }
    failModal.show();
}

export function showConfirmModal(message, title, callback) {
    document.getElementById("confirmModalConfirmButton").remove();
    const confirmButtonClone = confirmModalConfirmButton.cloneNode(true);
    if (message !== undefined) {
        confirmModalBody.innerText = message;
    }
    if (title !== undefined) {
        confirmModalTitle.innerText = title;
    }

    confirmButtonClone.addEventListener("click", () => {
        callback();
    });
    confirmModalCancelButton.after(confirmButtonClone);
    confirmModal.show();
}

export {successModal, successModalBody, failModal, failModalBody};
