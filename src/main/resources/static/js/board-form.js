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

//파라미터가 가진 노드 중 가장 마지막 노드로 포커싱을 한다
function focusToEnd(element) {
    selection = document.getSelection();
    range = document.createRange();
    selection.removeAllRanges();
    range.selectNodeContents(element);
    range.collapse(false);
    selection.addRange(range);
    element.focus();
}

//버튼의 글씨에 실제 적용될 크기를 반영 시켜 놓는다
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
                //문자를 입력하면 제한 글자 수와 바이트 수를 갱신한다
                elementManager.editor.updateCurrentSize();
                elementManager.editor.checkOverflow();

                //커서를 키보드로 옮겼을 때, 스타일이 적용되어 있으면 해당 버튼을 켜고 아니면 끈다
                Array.from(document.getElementsByClassName("btn-toggle")).forEach(
                    (toggleButton) => {
                        const style = toggleButton.getAttribute("data-style");
                        toggleButton.setAttribute("aria-pressed", document.queryCommandState(style).toString());
                    }
                )
            });
        });

        //커서를 마우스로 옮겼을 때, 스타일이 적용되어 있으면 해당 버튼을 켜고 아니면 끈다
        elementManager.editor.editorElement.addEventListener("click", function () {
            Array.from(document.getElementsByClassName("btn-toggle")).forEach(
                (toggleButton) => {
                    const style = toggleButton.getAttribute("data-style");
                    toggleButton.setAttribute("aria-pressed", document.queryCommandState(style).toString());
                }
            );
        });

        //(탭키 등으로)에디터에 포커스를 맞추었을 때, 스타일이 적용되어 있으면 해당 버튼을 켜고 아니면 끈다
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
        //토글 버튼 클릭을 해도 에디터에 초점이 남아있게 한다.
        Array.from(document.getElementsByClassName("btn-toggle")).forEach((button) => {
            button.addEventListener("mousedown", function (event) {
                event.preventDefault();
            });
        });

        //버튼이 눌리면 해당 버튼이 가지고 있는 스타일 (bold, italic...)을 적용한다
        Array.from(document.getElementsByClassName("btn-toggle")).forEach((button) => {
            button.addEventListener("click", function () {
                this.setAttribute("aria-pressed", button.getAttribute("aria-pressed") !== "true");
                document.execCommand(this.getAttribute("data-style"), false, null);
            });
        });
    }

    function addEventListenersToDropDownFontSizeBox() {
        //마우스를 드롭박스 위에 올리면 활성화 상태로 변경한다
        document.getElementsByClassName("dropdown-fontsize-box")[0].addEventListener("mouseover", function () {
            this.setAttribute("data-active", "true");
        });

        //드롭박스 위에서 클릭을 하면 비활성화 상태로 변경한다
        document.getElementsByClassName("dropdown-fontsize-box")[0].addEventListener("click", function () {
            this.setAttribute("data-active", "false");
        });

        //드롭다운 영역을 클릭 했을 때도 에디터에 초점이 남아있게 한다.
        document.getElementsByClassName("dropdown-fontsize-box")[0].addEventListener("mousedown", function (event) {
            event.preventDefault();
        });
    }

    function addEventListenersToTitle() {
        elementManager.title.titleElement.addEventListener("keydown", function (event) {
            //제목에서 엔터 키를 입력 하면 submit 하지 말고 대신 본문의 맨 끝으로 이동한다
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

    //버튼 자신이 가진 data-fontsize 속성 값을 기반으로 자신의 글자 크기를 키운다
    Array.from(document.getElementsByClassName("btn-dropdown-fontsize")).forEach((button) => {
        button.addEventListener("click", function () {
            document.execCommand("fontsize", false, button.getAttribute("data-fontsize"));
        });
    });

    //toolbar 버튼을 클릭하면 HTML 사이즈를 업데이트 한다
    document.getElementsByClassName("editor-toolbar")[0].addEventListener("click", function () {
        requestAnimationFrame(elementManager.editor.updateCurrentSize);
    });

    //버튼을 클릭해도 드롭박스가 닫히면 안 되므로 click 전파를 막는다
    document.getElementsByClassName("btn-fontsize")[0].addEventListener("click", function (event) {
        event.stopPropagation();
    });

    //에디터에서 작성한 내용을 input hidden 에 담는다
    document.forms["boardForm"].addEventListener("submit", function (event) {
            function existsOverflowedSize() {
                const isTitleSizeOverflowed = elementManager.title.overflowTitleSizeElement.getAttribute("data-overflow-titlesize") === "true";
                const isContentSizeOverflowed = elementManager.editor.overflowContentSizeElement.getAttribute("data-overflow-contentsize") === "true";
                const isHtmlSizeOverflowed = elementManager.editor.overflowHtmlSizeElement.getAttribute("data-overflow-htmlsize") === "true";
                return isTitleSizeOverflowed || isContentSizeOverflowed || isHtmlSizeOverflowed;
            }

            function existsEmptyValue() {
                function isTitleEmpty(){
                    const value = elementManager.title.titleElement.value;
                    return value.trim().length === 0;
                }

                function isContentEmpty() {
                    const value = elementManager.editor.editorElement.innerText;
                    return value.replaceAll("\n", "").trim().length === 0;
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