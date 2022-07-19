import {showFailModal, failModalBody} from "/js/util/modal.js";

(function execute() {
    if (failModalBody.innerText !== "") {
        showFailModal();
    }
})();
