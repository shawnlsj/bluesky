import {showFailModal} from "/js/util/modal.js";

(function execute() {
    let selection;
    let range;

    const elementManager = {
        form: document.getElementById("boardForm"),
        title: {
            getCurrentTitleLength: function () {
                return elementManager.title.titleElement.value.length;
            },
            updateCurrentLength: function () {
                elementManager.title.currentTitleLengthElement.textContent = elementManager.title.getCurrentTitleLength().toString();
            },
            checkOverflow: function () {
                const titleLength = elementManager.title.titleElement.value.length;
                if (titleLength > MAX_TITLE_LENGTH) {
                    elementManager.title.titleLengthWrapper.setAttribute("data-overflow-length", "true");
                } else {
                    elementManager.title.titleLengthWrapper.setAttribute("data-overflow-length", "false");
                }
            },
            titleElement: document.getElementById("title"),
            currentTitleLengthElement: document.getElementById("currentTitleLength"),
            titleLengthWrapper: document.getElementById("titleLengthWrapper")
        },
        editor: {
            getCurrentContentLength: function () {
                return elementManager.editor.editorElement.textContent
                    .replaceAll(/([\n\r])/g, "")
                    .length;
            },
            getCurrentHtmlLength: function () {
                return elementManager.editor.editorElement.innerHTML
                    .replaceAll("&nbsp;", " ")
                    .replaceAll(/([\n\r])/g, "<br>")
                    .length;
            },
            updateCurrentLength: function () {
                elementManager.editor.currentContentLengthElement.textContent = elementManager.editor.getCurrentContentLength().toString();
                elementManager.editor.currentHtmlLengthElement.textContent = elementManager.editor.getCurrentHtmlLength().toString();
            },
            checkOverflow: function () {
                const currentContentLength = elementManager.editor.editorElement.textContent.length;
                const currentHtmlLength = elementManager.editor.editorElement.innerHTML.length;

                if (currentContentLength > MAX_CONTENT_LENGTH) {
                    elementManager.editor.contentLengthWrapper.setAttribute("data-overflow-length", "true");
                } else {
                    elementManager.editor.contentLengthWrapper.setAttribute("data-overflow-length", "false");
                }

                if (currentHtmlLength > MAX_HTML_LENGTH) {
                    elementManager.editor.htmlLengthWrapper.setAttribute("data-overflow-length", "true");
                } else {
                    elementManager.editor.htmlLengthWrapper.setAttribute("data-overflow-length", "false");
                }
            },
            editorElement: document.getElementById("editor"),
            currentContentLengthElement: document.getElementById("currentContentLength"),
            currentHtmlLengthElement: document.getElementById("currentHtmlLength"),
            contentLengthWrapper: document.getElementById("contentLengthWrapper"),
            htmlLengthWrapper: document.getElementById("htmlLengthWrapper"),
        }
    };

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

    function initEventListeners() {
        addEventListenersToEditor();
        addEventListenersToToggleBtn();
        addEventListenersToDropDownFontSizeBox();
        addEventListenersToTitle();

        function addEventListenersToEditor() {
            elementManager.editor.editorElement.addEventListener("paste", function (event) {
                event.preventDefault();
                const text = event.clipboardData.getData("text/plain")
                    .replaceAll(/\t/g, " ");
                document.execCommand("insertText", false, text);
                elementManager.editor.editorElement.innerHTML =
                    elementManager.editor.editorElement.innerHTML.replaceAll(" ", "&nbsp;");
            })

            elementManager.editor.editorElement.addEventListener("cut", function () {
                requestAnimationFrame(elementManager.editor.updateCurrentLength);
            });

            elementManager.editor.editorElement.addEventListener("keydown", function () {
                requestAnimationFrame(() => {
                    //커서를 키보드로 옮겼을 때, 스타일이 적용되어 있으면 해당 버튼을 켜고 아니면 끈다
                    Array.from(document.getElementsByClassName("btn-toggle")).forEach(
                        (toggleButton) => {
                            const style = toggleButton.getAttribute("data-style");
                            toggleButton.setAttribute("aria-pressed", document.queryCommandState(style).toString());
                        }
                    )
                });
            });

            elementManager.editor.editorElement.addEventListener("input", function () {
                elementManager.editor.updateCurrentLength();
                elementManager.editor.checkOverflow();
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
            elementManager.title.titleElement.addEventListener("paste", function (event) {
                event.preventDefault();
                const text = event.clipboardData.getData("text/plain")
                    .replaceAll(/\t/g, " ");
                document.execCommand("insertText", false, text);
            })

            elementManager.title.titleElement.addEventListener("keydown", function (event) {
                //제목에서 엔터 키를 입력 하면 submit 하지 말고 대신 본문의 맨 끝으로 이동한다
                if (event.key === "Enter") {
                    event.preventDefault();
                    focusToEnd(elementManager.editor.editorElement);
                }
            });

            elementManager.title.titleElement.addEventListener("input", function () {
                elementManager.title.updateCurrentLength();
                elementManager.title.checkOverflow();
            });
        }

        window.addEventListener("pagehide", function () {
            function clearValues() {
                elementManager.form.reset();
                elementManager.editor.editorElement.innerHTML = "";
                elementManager.title.currentTitleLengthElement.textContent = "0";
                elementManager.editor.currentContentLengthElement.textContent = "0";
                elementManager.editor.currentHtmlLengthElement.textContent = "0";
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
            requestAnimationFrame(elementManager.editor.updateCurrentLength);
        });

        //버튼을 클릭해도 드롭박스가 닫히면 안 되므로 click 전파를 막는다
        document.getElementsByClassName("btn-fontsize")[0].addEventListener("click", function (event) {
            event.stopPropagation();
        });

        elementManager.form.addEventListener("submit", function (event) {
                function existsOverflowedLength() {
                    const isTitleLengthOverflowed = elementManager.title.titleLengthWrapper.getAttribute("data-overflow-length") === "true";
                    const isContentLengthOverflowed = elementManager.editor.contentLengthWrapper.getAttribute("data-overflow-length") === "true";
                    const isHtmlLengthOverflowed = elementManager.editor.htmlLengthWrapper.getAttribute("data-overflow-length") === "true";
                    return isTitleLengthOverflowed || isContentLengthOverflowed || isHtmlLengthOverflowed;
                }

                function existsEmptyValue() {
                    function isTitleEmpty() {
                        return elementManager.title.titleElement.value.trim() === "";
                    }

                    function isContentEmpty() {
                        return elementManager.editor.editorElement.textContent.trim() === "";
                    }

                    return isTitleEmpty() || isContentEmpty();
                }

                event.preventDefault();
                if (existsEmptyValue()) {
                    showFailModal("제목과 본문은 공백일 수 없습니다.");
                } else if (existsOverflowedLength()) {
                    showFailModal("제한된 글자 수를 초과하였습니다.");
                } else {
                    document.getElementsByName("content")[0].value
                        = innerHtmlToFormValue(elementManager.editor.editorElement.innerHTML);
                    fetch("/board" + (boardId == null ? "" : "/" + boardId), {
                        method: httpMethod,
                        body: new FormData(elementManager.form),
                    }).then(response => {
                        switch (response.status) {
                            case 200:
                                toPreviousPage();
                                break;
                            case 400:
                                showFailModal("잘못된 요청입니다.");
                                break;
                            case 500:
                                showFailModal("서버에 오류가 발생하였습니다.");
                                break;
                            default:
                                throw new Error();
                        }
                    }).catch(() => showFailModal("오류가 발생하였습니다."));
                }
            }
        );
    }

    function innerHtmlToFormValue(innerHtml) {
        return innerHtml
            .replaceAll(/(&nbsp;|\t)/g, " ")
            .replaceAll(/([\n\r])/g, "<br>")
    }

    initEventListeners();
    document.getElementById("maxTitleLength").textContent = MAX_TITLE_LENGTH;
    document.getElementById("maxContentLength").textContent = MAX_CONTENT_LENGTH;
    document.getElementById("maxHtmlLength").textContent = MAX_HTML_LENGTH;
    elementManager.title.titleElement.dispatchEvent(new Event("input"));
    elementManager.editor.editorElement.dispatchEvent(new Event("input"));
    elementManager.title.titleElement.focus();
})();
