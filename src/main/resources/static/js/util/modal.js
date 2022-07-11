const successModalElement = document.getElementById("successModal");
const successModalBody = document.getElementById("successModalBody");
const successModal = new bootstrap.Modal(successModalElement);
const failModalElement = document.getElementById("failModal");
const failModalBody = document.getElementById("failModalBody");
const failModal = new bootstrap.Modal(failModalElement)

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
