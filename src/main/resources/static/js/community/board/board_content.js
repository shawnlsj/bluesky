import {showConfirmModal, showFailModal} from "/js/util/modal.js";

class ReplyForm {
    constructor(container) {
        this.container = container;
        this.replyEditor = this.container.querySelector("[data-element-name='replyEditor']");
        this.contentField = this.container.querySelector("[data-element-name='contentField']");
        this.replyIdField = this.container.querySelector("[data-element-name='replyIdField']");
        this.contentLengthWrapper = this.container.querySelector("[data-element-name='contentLengthWrapper']");
        this.currentContentLength = this.container.querySelector("[data-element-name='currentContentLength']");
        this.submitButton = this.container.querySelector("[data-element-name='replySubmitButton']");
        this.submitButtonSpinner = this.container.querySelector("[data-element-name='replySubmitButtonSpinner']");
    }

    removeContainer() {
        this.container.remove();
    }

    showContainer() {
        displayOn(this.container);
    }

    spinnerOn() {
        displayOff(this.submitButton);
        displayOn(this.submitButtonSpinner);
    }

    spinnerOff() {
        displayOff(this.submitButtonSpinner);
        displayOn(this.submitButton);
    }
}

class ReplySaveForm extends ReplyForm {
    static findContainer(element) {
        return element.closest("[data-element-name='replySaveFormContainer']");
    }

    static findNestedReplyContainer(element) {
        return element.closest("[data-element-name='nestedReplySaveFormContainer']");
    }

    constructor(container) {
        super(container);
        this.form = container.querySelector("[data-element-name='replySaveForm']");
    }

    removeTopBorder() {
        this.container.classList.remove("border-top-gray");
    }
}

class ReplyUpdateForm extends ReplyForm {
    static findContainer(element) {
        return element.closest("[data-element-name='replyUpdateFormContainer']");
    }

    constructor(container, reply) {
        super(container);
        this.form = container.querySelector("[data-element-name='replyUpdateForm']");
        this.reply = reply;

        if (reply !== undefined) {
            //댓글을 숨김
            reply.hideContentWrapper();

            //원래 댓글의 내용을 에디터로 복사
            this.replyEditor.value = reply.textContent;

            //댓글 안으로 수정 폼을 붙이고
            //글자 수 초기화를 위해 input 이벤트 발행
            reply.writerWrapper.after(this.container);
            this.replyEditor.dispatchEvent(new Event("input", {bubbles: true}));

            //댓글 id를 input hidden 에 저장
            this.replyIdField.value = reply.id;

            //수정 폼을 화면에 출력
            displayOn(this.container);
        }
    }

    cancel() {
        this.removeContainer();
        if (this.reply !== undefined) {
            this.reply.showContentWrapper();
        }
    }
}

class Reply {
    static findReplyItem(element) {
        return element.closest("[data-element-name='replyItem']");
    }

    static findReply(element) {
        return new Reply(element.closest("[data-element-name='replyItem']"));
    }

    constructor(replyItem) {
        this.container = replyItem;
        this.id = parseInt(replyItem.getAttribute("data-reply-id"));
        this.likesCount = replyItem.querySelector("[data-element-name='replyLikesCount']");
        this.writerWrapper = replyItem.querySelector("[data-element-name='replyWriterWrapper']");
        this.contentWrapper = replyItem.querySelector("[data-element-name='replyContentWrapper']");
        this.writer = replyItem.querySelector("[data-element-name='replyWriter']").textContent;
        this.htmlContent = replyItem.querySelector("[data-element-name='replyContent']").innerHTML;
    }

    hasBottomBorder() {
        return this.container.classList.contains("border-bottom-gray");
    }

    showContentWrapper() {
        displayOn(this.contentWrapper);
    }

    hideContentWrapper() {
        displayOff(this.contentWrapper);
    }

    get textContent() {
        return this.htmlContent.replaceAll("<br>", "\n")
            .replaceAll("&amp;", "&")
            .replaceAll("&lt;", "<")
            .replaceAll("&gt;", ">");
    }
}

(function execute() {
    //게시글 관련 엘리먼트
    const boardLikesButton = document.getElementById("boardLikesButton");
    const boardLikesOnMark = document.getElementById("boardLikesOnMark");
    const boardLikesOffMark = document.getElementById("boardLikesOffMark");
    const boardLikesCount = document.getElementById("boardLikesCount");
    const boardDeleteButton = document.getElementById("boardDeleteButton");

    //댓글 관련 엘리먼트
    const replyContainer = document.getElementById("replyContainer");
    const replyList = document.getElementById("replyList");

    //답글 폼 컨테이너
    const nestedReplySaveFormContainer =
        document.querySelector("[data-element-name='nestedReplySaveFormContainer']");
    let nestedReplySaveForm = new ReplySaveForm(nestedReplySaveFormContainer.cloneNode(true));

    //댓글 수정 폼 컨테이너
    const replyUpdateFormContainer =
        document.querySelector("[data-element-name='replyUpdateFormContainer']");
    let replyUpdateForm = new ReplyUpdateForm(replyUpdateFormContainer.cloneNode(true));

    function deleteBoard(boardId) {
        const failModalTitle = "게시글 삭제 실패";
        fetch("/board/" + boardId, {
            method: "DELETE",
        }).then(async response => {
            switch (response.status) {
                case 200:
                    toPreviousPage();
                    break;
                case 400:
                    showFailModal("잘못된 요청입니다", failModalTitle);
                    break;
                case 404:
                    response.json().then(result => showFailModal(result.message, failModalTitle));
                    break;
                case 500:
                    showFailModal("서버에 오류가 발생하였습니다", failModalTitle);
                    break;
            }
        }).catch(() => {
                showFailModal("오류가 발생하였습니다.", failModalTitle);
            }
        );
    }

    function deleteReply(replyId) {
        const failModalTitle = "댓글 삭제 실패";
        fetch("/reply/" + replyId, {
            method: "DELETE",
        }).then(async response => {
            switch (response.status) {
                case 200:
                    loadReplyList();
                    break;
                case 400:
                    showFailModal("잘못된 요청입니다", failModalTitle);
                    break;
                case 404:
                    response.json().then(result => showFailModal(result.message, failModalTitle));
                    break;
                case 500:
                    showFailModal("서버에 오류가 발생하였습니다", failModalTitle);
                    break;
            }
        }).catch(() => {
                showFailModal("오류가 발생하였습니다.", failModalTitle);
            }
        );
    }

    async function loadReplyList(page) {
        if (page === undefined) {
            page = 1;
        }
        const failModalTitle = "댓글 불러오기 실패";
        return fetch("/reply?" + new URLSearchParams({
            boardId: boardId,
            page: page
        })).then(response => {
            switch (response.status) {
                case 200:
                    return response.text();
                case 404:
                    showFailModal("삭제된 게시글입니다", failModalTitle)
                    break;
                case 500:
                    showFailModal("서버에 오류가 발생하였습니다", failModalTitle);
                    break;
            }
        }).then(html => {
            if (html !== undefined) {
                replyList.innerHTML = html;
            }
        }).catch(() => showFailModal("오류가 발생하였습니다", failModalTitle));
    }

    replyContainer.addEventListener("input", function (event) {
        if (event.target.getAttribute("data-element-name") === "replyEditor") {
            let replyForm;
            if (ReplySaveForm.findContainer(event.target) !== null) {
                replyForm = new ReplySaveForm(ReplySaveForm.findContainer(event.target));
            } else if (ReplySaveForm.findNestedReplyContainer(event.target) !== null) {
                replyForm = new ReplySaveForm(ReplySaveForm.findNestedReplyContainer(event.target));
            } else {
                replyForm = new ReplyUpdateForm(ReplyUpdateForm.findContainer(event.target));
            }

            const replyEditor = replyForm.replyEditor;
            const contentLengthWrapper = replyForm.contentLengthWrapper;
            const currentLength = replyEditor.value.length;

            replyForm.currentContentLength.textContent = currentLength;
            if (currentLength > MAX_CONTENT_LENGTH) {
                contentLengthWrapper.setAttribute("data-overflow-length", "true");
            } else {
                contentLengthWrapper.setAttribute("data-overflow-length", "false");
            }
        }
    });

    replyContainer.addEventListener("paste", function (event) {
        event.preventDefault();
        const text = event.clipboardData.getData("text/plain")
            .replaceAll("\r", "")
            .replaceAll("\t", " ");
        document.execCommand("insertText", false, text);
    });

    replyContainer.addEventListener("submit", function (event) {
        event.preventDefault();

        //수정 폼을 전송하는 경우 http 메소드를 PUT 으로 변경
        let httpMethod = "POST";
        if (event.target.getAttribute("data-element-name") === "replyUpdateForm") {
            httpMethod = "PUT";
        }

        //전송할 form 을 초기화
        //수정 폼을 전송하는 경우 path variable 을 추가로 설정
        let replyForm;
        let replyIdPathVariable = "";
        if (ReplySaveForm.findContainer(event.target) !== null) {
            replyForm = new ReplySaveForm(ReplySaveForm.findContainer(event.target));
        } else if (ReplySaveForm.findNestedReplyContainer(event.target) !== null) {
            replyForm = new ReplySaveForm(ReplySaveForm.findNestedReplyContainer(event.target));
        } else {
            replyForm = new ReplyUpdateForm(ReplyUpdateForm.findContainer(event.target));
            replyIdPathVariable = "/" + replyForm.replyIdField.value;
        }
        const replyEditor = replyForm.replyEditor;
        const failModalTitle = "댓글 등록 실패";

        //버튼의 실제 계산된 넓이를 spinner 에게 그대로 부여한다.
        if (replyForm.submitButtonSpinner.style.width === "") {
            replyForm.submitButtonSpinner.style.width =
                replyForm.submitButton.getBoundingClientRect().width + "px";
        }

        //전송 버튼을 감추고 대신 스피너를 표시
        replyForm.spinnerOn();

        if (replyEditor.value.trim() === "") {
            showFailModal("최소 1자 이상 입력해주세요.", failModalTitle);
            replyForm.spinnerOff();
            return;
        }
        if (replyEditor.value.length > MAX_CONTENT_LENGTH) {
            showFailModal("길이 제한을 초과하였습니다.", failModalTitle);
            replyForm.spinnerOff();
            return;
        }

        replyForm.contentField.value = replyEditor.value
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll("\n", "<br>");

        fetch("/reply" + replyIdPathVariable, {
            method: httpMethod,
            body: new FormData(replyForm.form),
        }).then(async response => {
            switch (response.status) {
                case 200:
                    //댓글이 로딩 되면 폼을 리셋 하고
                    //글자 수 초기화를 위해 input 이벤트 발행
                    await loadReplyList();
                    replyForm.form.reset();
                    replyForm.replyEditor.dispatchEvent(new Event("input", {bubbles: true}));
                    break;
                case 400:
                    showFailModal("잘못된 요청입니다", failModalTitle);
                    break;
                case 404:
                    response.json().then(result => showFailModal(result.message, failModalTitle));
                    break;
                case 500:
                    showFailModal("서버에 오류가 발생하였습니다", failModalTitle);
                    break;
            }
            replyForm.spinnerOff();
        }).catch(() => {
                showFailModal("오류가 발생하였습니다.", failModalTitle);
                replyForm.spinnerOff();
            }
        );

    });

    replyContainer.addEventListener("click", function (event) {
        //페이지 버튼을 눌렀을 경우
        if (event.target.getAttribute("data-element-name") === "pageButton") {
            loadReplyList(event.target.getAttribute("data-page"));

            //댓글 좋아요를 눌렀을 경우
        } else if (event.target.getAttribute("data-element-name") === "replyLikesButton") {
            const replyLikesButton = event.target;
            const replyLikesOnMark = replyLikesButton.querySelector("[data-element-name='replyLikesOnMark']");
            const replyLikesOffMark = replyLikesButton.querySelector("[data-element-name='replyLikesOffMark']");

            //부모 요소를 탐색하여 부모로부터 likes 카운트 요소를 탐색
            const reply = new Reply(Reply.findReplyItem(replyLikesButton));
            const replyLikesCount = reply.likesCount;
            if (!isLoginUser) {
                showFailModal("로그인 후 이용하여 주세요.", "비로그인 기능 제한 안내");
                return;
            }

            const failModalTitle = "기능 수행 실패";
            fetch("/reply/toggle-likes?" + new URLSearchParams({
                replyId: reply.id,
            })).then(response => {
                switch (response.status) {
                    case 200:
                        //하트가 채워져 있는 상태면 카운트를 -1 한다
                        //채워져 있지 않은 상태면 카운트를 +1 한다
                        if (replyLikesOnMark.getAttribute("data-display") === "true") {
                            replyLikesCount.textContent = (parseInt(replyLikesCount.textContent) - 1).toString();
                        } else {
                            replyLikesCount.textContent = (parseInt(replyLikesCount.textContent) + 1).toString();
                        }
                        toggleDisplayStatus(replyLikesOnMark);
                        toggleDisplayStatus(replyLikesOffMark);
                        break;
                    case 404:
                        showFailModal("삭제되었거나 존재하지 않는 댓글입니다.", failModalTitle)
                        break
                    case 500:
                        showFailModal("서버에 오류가 발생하였습니다.", failModalTitle);
                        break;
                    default:
                        throw new Error();
                }
            }).catch(() => showFailModal("오류가 발생하였습니다.", failModalTitle));

            //답글 쓰기를 눌렀을 경우
        } else if (event.target.getAttribute("data-element-name") === "nestedReplyButton") {
            if (!isLoginUser) {
                showFailModal("로그인 후 이용하여 주세요.", "비로그인 기능 제한 안내")
                return;
            }
            const nestedReplyButton = event.target;
            const reply = new Reply(Reply.findReplyItem(nestedReplyButton));
            const replyId = reply.id;

            //기존 답글 쓰기 창을 없애버리고
            //기존 수정 창을 취소 시킴
            nestedReplySaveForm.removeContainer();
            replyUpdateForm.cancel();

            //새로운 폼을 생성
            nestedReplySaveForm =
                new ReplySaveForm(nestedReplySaveFormContainer.cloneNode(true));

            //댓글과 border 가 겹친다면 복사본의 border 을 제거
            if (reply.hasBottomBorder()) {
                nestedReplySaveForm.removeTopBorder();
            }

            //부모 댓글 id 를 input hidden 에 입력
            nestedReplySaveForm.replyIdField.value = replyId;

            //placeholder 에 부모 댓글 작성자의 닉네임을 표시
            nestedReplySaveForm.replyEditor.placeholder
                = "@" + reply.writer;

            //댓글 뒤에 복사본을 붙임
            reply.container.after(nestedReplySaveForm.container);
            nestedReplySaveForm.showContainer();

            //수정을 눌렀을 경우
        } else if (event.target.getAttribute("data-element-name") === "replyModifyButton") {
            const reply = new Reply(Reply.findReplyItem(event.target));

            //기존 답글 쓰기 창을 없애버리고
            //기존 수정 창을 취소 시킴
            nestedReplySaveForm.removeContainer();
            replyUpdateForm.cancel();

            //새로운 폼을 생성
            replyUpdateForm =
                new ReplyUpdateForm(replyUpdateFormContainer.cloneNode(true), reply);

            //수정 취소를 눌렀을 경우
        } else if (event.target.getAttribute("data-element-name") === "replyCancelButton") {
            replyUpdateForm.cancel();

            //삭제를 눌렀을 경우
        } else if (event.target.getAttribute("data-element-name") === "replyDeleteButton") {
          const reply = Reply.findReply(event.target);
            showConfirmModal("정말로 삭제하시겠습니까?", "댓글 삭제 확인"
                , deleteReply.bind(null, reply.id));
        }
    });

    boardDeleteButton?.addEventListener("click", function () {
        showConfirmModal("정말로 삭제하시겠습니까?", "게시글 삭제 확인"
            , deleteBoard.bind(null, boardId));
    });

    boardLikesButton.addEventListener("click", function () {
        if (!isLoginUser) {
            showFailModal("로그인 후 이용하여 주세요.", "비로그인 기능 제한 안내");
            return;
        }

        const failModalTitle = "기능 수행 실패";
        fetch("/board/toggle-likes?" + new URLSearchParams({
            boardId: boardId,
        })).then(response => {
            switch (response.status) {
                case 200:
                    //하트가 채워져 있는 상태면 카운트를 -1 한다
                    //채워져 있지 않은 상태면 카운트를 +1 한다
                    if (boardLikesOnMark.getAttribute("data-display") === "true") {
                        boardLikesCount.textContent = (parseInt(boardLikesCount.textContent) - 1).toString();
                    } else {
                        boardLikesCount.textContent = (parseInt(boardLikesCount.textContent) + 1).toString();
                    }
                    toggleDisplayStatus(boardLikesOnMark);
                    toggleDisplayStatus(boardLikesOffMark);
                    break;
                case 404:
                    showFailModal("삭제되었거나 존재하지 않는 게시글입니다.", failModalTitle)
                    break
                case 500:
                    showFailModal("서버에 오류가 발생하였습니다.", failModalTitle);
                    break;
                default:
                    throw new Error();
            }
        }).catch(() => showFailModal("오류가 발생하였습니다.", failModalTitle));
    });
})();
