const editor = document.getElementsByClassName("editor")[0];
let selection;
let range;

window.onload = init;

function init() {
    initFontSize();
    focusToTitle();
    initEventListeners();
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

function focusToTitle() {
    document.getElementById("title").focus();
}

function initEventListeners() {

    //토글 버튼 클릭을 해도 에디터에 초점이 남아있게 한다.
    Array.from(document.getElementsByClassName("btn-toggle")).forEach((button) => {
        button.addEventListener("mousedown", function (event) {
            event.preventDefault();
        });
    });

    //드롭다운 영역을 클릭 했을 때도 에디터에 초점이 남아있게 한다.
    document.querySelector("div.dropdown-fontsize-box").addEventListener("mousedown", function(event){
       event.preventDefault();
    });


    //제목에서 엔터 키를 입력 하면 submit 하지 말고 대신 본문의 맨 끝으로 이동한다.
    document.getElementById("title").addEventListener("keydown", function (event) {
        if (event.key === "Enter") {
            event.preventDefault();
            focusToEnd(editor);
        }
    });

    //버튼이 눌리면 해당 버튼이 가지고 있는 스타일 (bold, italic...)을 적용한다
    Array.from(document.getElementsByClassName("btn-toggle")).forEach((button) => {
        button.addEventListener("click", function () {
            this.setAttribute("aria-pressed", button.getAttribute("aria-pressed") !== "true");


            document.execCommand(this.getAttribute("data-style"), false, null);
        });
    });

    //커서를 키보드로 옮겼을 때, 스타일이 적용되어 있으면 해당 버튼을 켜고 아니면 끈다
    editor.addEventListener("keydown", function () {
        requestAnimationFrame(() => Array.from(document.getElementsByClassName("btn-toggle")).forEach(
            (toggleButton) => {
                const style = toggleButton.getAttribute("data-style");
                toggleButton.setAttribute("aria-pressed", document.queryCommandState(style).toString());
            }
        ));
    });

    //커서를 마우스로 옮겼을 때, 스타일이 적용되어 있으면 해당 버튼을 켜고 아니면 끈다
    editor.addEventListener("click", function () {
        Array.from(document.getElementsByClassName("btn-toggle")).forEach(
            (toggleButton) => {
                const style = toggleButton.getAttribute("data-style");
                toggleButton.setAttribute("aria-pressed", document.queryCommandState(style).toString());
            }
        );
    });

    //(탭키 등으로)에디터에 포커스를 맞추었을 때, 스타일이 적용되어 있으면 해당 버튼을 켜고 아니면 끈다
    editor.addEventListener("focusin", function () {
        Array.from(document.getElementsByClassName("btn-toggle")).forEach(
            (toggleButton) => {
                const style = toggleButton.getAttribute("data-style");
                toggleButton.setAttribute("aria-pressed", document.queryCommandState(style).toString());
            }
        )
    });

    //버튼의 data-fontsize 값으로 글자 크기를 키운다.
    Array.from(document.getElementsByClassName("btn-dropdown-fontsize")).forEach((button) => {
        button.addEventListener("click", function () {
            document.execCommand("fontsize", false, button.getAttribute("data-fontsize"));
        });
    });


    //마우스를 드롭박스 위에 올리면 활성화 상태로 변경한다
    document.getElementsByClassName("dropdown-fontsize-box")[0].addEventListener("mouseover", function () {
        this.setAttribute("data-active", "true");
    });

    //드롭박스 위에서 클릭을 하면 비활성화 상태로 변경한다
    document.getElementsByClassName("dropdown-fontsize-box")[0].addEventListener("click", function () {
        this.setAttribute("data-active", "false");
    });

    //버튼을 클릭해도 드롭박스가 닫히면 안 되므로 click 전파를 막는다
    document.getElementsByClassName("btn-fontsize")[0].addEventListener("click", function (event) {
        event.stopPropagation();
    });
}










