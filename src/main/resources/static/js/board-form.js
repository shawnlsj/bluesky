import messages from "./messages.js";

window.onload = init;

let selection;
let range;

const elementManager = {
    title: {
        updateCurrentSize: function () {
            const titleSize = elementManager.title.titleElement.value.length;
            elementManager.title.currentTitleSizeElement.setAttribute("data-current-titlesize", titleSize);
            elementManager.title.currentTitleSizeElement.textContent = titleSize;
        },
        checkOverflow: function () {
            const titleSize = Number(elementManager.title.currentTitleSizeElement.getAttribute("data-current-titlesize"));
            if (titleSize > elementManager.title.MAX_TITLE_SIZE) {
                elementManager.title.overflowTitleSizeElement.setAttribute("data-overflow-titlesize", "true");
            } else {
                elementManager.title.overflowTitleSizeElement.setAttribute("data-overflow-titlesize", "false");
            }
        },
        titleElement: document.getElementById("title"),
        currentTitleSizeElement: document.querySelector("[data-current-titlesize]"),
        maxTitleSizeElement: document.querySelector("[data-max-titlesize]"),
        MAX_TITLE_SIZE: Number(document.querySelector("[data-max-titlesize]").getAttribute("data-max-titlesize")),
        overflowTitleSizeElement: document.querySelector("[data-overflow-titlesize]")
    },
    editor: {
        updateCurrentSize: function () {
            const currentFontSize = elementManager.editor.editorElement.textContent.length;
            const currentHtmlSize = elementManager.editor.editorElement.innerHTML.length;

            elementManager.editor.currentContentSizeElement.setAttribute("data-current-contentsize", currentFontSize.toString());
            elementManager.editor.currentHtmlSizeElement.setAttribute("data-current-htmlsize", currentHtmlSize.toString());

            elementManager.editor.currentContentSizeElement.textContent = currentFontSize.toString();
            elementManager.editor.currentHtmlSizeElement.textContent = currentHtmlSize.toString();
        },
        checkOverflow: function () {
            const contentSize = Number(elementManager.editor.currentContentSizeElement.getAttribute("data-current-contentsize"));
            const htmlSize = Number(elementManager.editor.currentHtmlSizeElement.getAttribute("data-current-htmlsize"));

            if (contentSize > elementManager.editor.MAX_CONTENT_SIZE) {
                elementManager.editor.overflowContentSizeElement.setAttribute("data-overflow-contentsize", "true");
            } else {
                elementManager.editor.overflowContentSizeElement.setAttribute("data-overflow-contentsize", "false");
            }

            if (htmlSize > elementManager.editor.MAX_HTML_SIZE) {
                elementManager.editor.overflowHtmlSizeElement.setAttribute("data-overflow-htmlsize", "true");
            } else {
                elementManager.editor.overflowHtmlSizeElement.setAttribute("data-overflow-htmlsize", "false");
            }
        },
        editorElement: document.getElementsByClassName("editor")[0],
        currentContentSizeElement: document.querySelector("[data-current-contentsize]"),
        currentHtmlSizeElement: document.querySelector("[data-current-htmlsize]"),
        maxContentSizeElement: document.querySelector("[data-max-contentsize]"),
        maxHtmlSizeElement: document.querySelector("[data-max-htmlsize]"),
        MAX_CONTENT_SIZE: Number(document.querySelector("[data-max-contentsize]").getAttribute("data-max-contentsize")),
        MAX_HTML_SIZE: Number(document.querySelector("[data-max-htmlsize]").getAttribute("data-max-htmlsize")),
        overflowContentSizeElement: document.querySelector("[data-overflow-contentsize]"),
        overflowHtmlSizeElement: document.querySelector("[data-overflow-htmlsize]")
    }
};

function init() {
    initFontSize();
    initEventListeners();
    elementManager.title.titleElement.focus();
}

//??????????????? ?????? ?????? ??? ?????? ????????? ????????? ???????????? ??????
function focusToEnd(element) {
    selection = document.getSelection();
    range = document.createRange();
    selection.removeAllRanges();
    range.selectNodeContents(element);
    range.collapse(false);
    selection.addRange(range);
    element.focus();
}

//????????? ????????? ?????? ????????? ????????? ?????? ?????? ?????????
function initFontSize() {
    Array.from(document.getElementsByClassName("btn-dropdown-fontsize")).forEach((button) => {
        selection = document.getSelection();
        range = document.createRange();

        range.selectNodeContents(button);

        selection.removeAllRanges();
        selection.addRange(range);

        button.style.display = "block";
        button.setAttribute("contenteditable", "true");
        document.execCommand("fontSize", false, button.getAttribute("data-fontsize"));
        button.removeAttribute("contenteditable");
        button.removeAttribute("style");
    });
}

function initEventListeners() {
    addEventListenersToEditor();
    addEventListenersToToggleBtn();
    addEventListenersToDropDownFontSizeBox();
    addEventListenersToTitle();

    function addEventListenersToEditor() {
        elementManager.editor.editorElement.addEventListener("paste", function (event) {
            event.preventDefault();
            const text = event.clipboardData.getData("text/plain");
            document.execCommand("insertText", false, text);
        })
        elementManager.editor.editorElement.addEventListener("cut", function () {
            requestAnimationFrame(elementManager.editor.updateCurrentSize);

        });
        elementManager.editor.editorElement.addEventListener("keydown", function () {
            requestAnimationFrame(() => {
                //????????? ???????????? ?????? ?????? ?????? ????????? ?????? ????????????
                elementManager.editor.updateCurrentSize();
                elementManager.editor.checkOverflow();

                //????????? ???????????? ????????? ???, ???????????? ???????????? ????????? ?????? ????????? ?????? ????????? ??????
                Array.from(document.getElementsByClassName("btn-toggle")).forEach(
                    (toggleButton) => {
                        const style = toggleButton.getAttribute("data-style");
                        toggleButton.setAttribute("aria-pressed", document.queryCommandState(style).toString());
                    }
                )
            });
        });

        //????????? ???????????? ????????? ???, ???????????? ???????????? ????????? ?????? ????????? ?????? ????????? ??????
        elementManager.editor.editorElement.addEventListener("click", function () {
            Array.from(document.getElementsByClassName("btn-toggle")).forEach(
                (toggleButton) => {
                    const style = toggleButton.getAttribute("data-style");
                    toggleButton.setAttribute("aria-pressed", document.queryCommandState(style).toString());
                }
            );
        });

        //(?????? ?????????)???????????? ???????????? ???????????? ???, ???????????? ???????????? ????????? ?????? ????????? ?????? ????????? ??????
        elementManager.editor.editorElement.addEventListener("focusin", function () {
            Array.from(document.getElementsByClassName("btn-toggle")).forEach(
                (toggleButton) => {
                    const style = toggleButton.getAttribute("data-style");
                    toggleButton.setAttribute("aria-pressed", document.queryCommandState(style).toString());
                }
            )
        });
    }

    function addEventListenersToToggleBtn() {
        //?????? ?????? ????????? ?????? ???????????? ????????? ???????????? ??????.
        Array.from(document.getElementsByClassName("btn-toggle")).forEach((button) => {
            button.addEventListener("mousedown", function (event) {
                event.preventDefault();
            });
        });

        //????????? ????????? ?????? ????????? ????????? ?????? ????????? (bold, italic...)??? ????????????
        Array.from(document.getElementsByClassName("btn-toggle")).forEach((button) => {
            button.addEventListener("click", function () {
                this.setAttribute("aria-pressed", button.getAttribute("aria-pressed") !== "true");
                document.execCommand(this.getAttribute("data-style"), false, null);
            });
        });
    }

    function addEventListenersToDropDownFontSizeBox() {
        //???????????? ???????????? ?????? ????????? ????????? ????????? ????????????
        document.getElementsByClassName("dropdown-fontsize-box")[0].addEventListener("mouseover", function () {
            this.setAttribute("data-active", "true");
        });

        //???????????? ????????? ????????? ?????? ???????????? ????????? ????????????
        document.getElementsByClassName("dropdown-fontsize-box")[0].addEventListener("click", function () {
            this.setAttribute("data-active", "false");
        });

        //???????????? ????????? ?????? ?????? ?????? ???????????? ????????? ???????????? ??????.
        document.getElementsByClassName("dropdown-fontsize-box")[0].addEventListener("mousedown", function (event) {
            event.preventDefault();
        });
    }

    function addEventListenersToTitle() {
        elementManager.title.titleElement.addEventListener("keydown", function (event) {
            //???????????? ?????? ?????? ?????? ?????? submit ?????? ?????? ?????? ????????? ??? ????????? ????????????
            if (event.key === "Enter") {
                event.preventDefault();
                focusToEnd(elementManager.editor.editorElement);
            } else {
                requestAnimationFrame(() => {
                    elementManager.title.updateCurrentSize();
                    elementManager.title.checkOverflow();
                });
            }
        });
    }

    window.addEventListener("pagehide", function () {
        function clearValues() {
            elementManager.title.titleElement.value = "";
            elementManager.editor.editorElement.innerHTML = "";
            elementManager.title.currentTitleSizeElement.textContent = "0";
            elementManager.editor.currentContentSizeElement.textContent = "0";
            elementManager.editor.currentHtmlSizeElement.textContent = "0";
        }

        clearValues();
    });

    //?????? ????????? ?????? data-fontsize ?????? ?????? ???????????? ????????? ?????? ????????? ?????????
    Array.from(document.getElementsByClassName("btn-dropdown-fontsize")).forEach((button) => {
        button.addEventListener("click", function () {
            document.execCommand("fontsize", false, button.getAttribute("data-fontsize"));
        });
    });

    //toolbar ????????? ???????????? HTML ???????????? ???????????? ??????
    document.getElementsByClassName("editor-toolbar")[0].addEventListener("click", function () {
        requestAnimationFrame(elementManager.editor.updateCurrentSize);
    });

    //????????? ???????????? ??????????????? ????????? ??? ????????? click ????????? ?????????
    document.getElementsByClassName("btn-fontsize")[0].addEventListener("click", function (event) {
        event.stopPropagation();
    });

    //??????????????? ????????? ????????? input hidden ??? ?????????
    document.forms["boardForm"].addEventListener("submit", function (event) {
            function existsOverflowedSize() {
                const isTitleSizeOverflowed = elementManager.title.overflowTitleSizeElement.getAttribute("data-overflow-titlesize") == "true";
                const isContentSizeOverflowed = elementManager.editor.overflowContentSizeElement.getAttribute("data-overflow-contentsize") == "true";
                const isHtmlSizeOverflowed = elementManager.editor.overflowHtmlSizeElement.getAttribute("data-overflow-htmlsize") == "true";
                return isTitleSizeOverflowed || isContentSizeOverflowed || isHtmlSizeOverflowed;
            }

            function existsEmptyValue() {
                function isTitleEmpty(){
                    const value = elementManager.title.titleElement.value;
                    return value.trim().length == 0;
                }

                function isContentEmpty() {
                    const value = elementManager.editor.editorElement.innerText;
                    return value.replaceAll("\n", "").trim().length == 0;
                }
                return isTitleEmpty() || isContentEmpty();
            }

            if (existsEmptyValue()) {
                event.preventDefault();
                alert(messages.alert.notBlank);
            } else if (existsOverflowedSize()) {
                event.preventDefault();
                alert(messages.alert.range);
            } else {
                document.getElementsByName("content")[0].value
                    = document.getElementsByClassName("editor")[0].innerHTML;
            }
        }
    );
}