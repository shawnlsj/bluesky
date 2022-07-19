(function execute() {
    function checkProtocol() {
        if (location.protocol === "http:") {
            let url = "https:" + location.href.substring(location.protocol.length);
            location.replace(url);
        }
    }

    function welcomeMessage() {
        const style="color:white;font-size:24px;background-color:#5555ff;line-height:70px;padding-left:10px;padding-right:10px"
        console.log("%c bluesky를 방문해 주셔서 감사합니다! " , style);
    }
    checkProtocol();
    welcomeMessage();
})();

function displayOn(element) {
    element.setAttribute("data-display", "true");
}

function displayOff(element) {
    element.setAttribute("data-display", "false");
}

function enableValid(inputElement) {
    disableInvalid(inputElement);
    inputElement.classList.add("is-valid");
}

function enableInvalid(inputElement) {
    disableValid(inputElement);
    inputElement.classList.add("is-invalid");
}

function disableValid(inputElement) {
    inputElement.classList.remove("is-valid");
}

function disableInvalid(inputElement) {
    inputElement.classList.remove("is-invalid");
}

function isValidStatus(inputElement) {
   return inputElement.classList.contains("is-valid");
}

function isInvalidStatus(inputElement) {
   return inputElement.classList.contains("is-invalid");
}

function hasWhiteSpace(string) {
    return /\s/.test(string);
}
