import {MAX_NICKNAME_LENGTH, MIN_NICKNAME_LENGTH} from "/js/config/user_option.js";
class NicknameValidator{
    static nicknameForm = document.getElementById("nickname");
    static nicknameInvalidFeedback = document.getElementById("nicknameInvalidFeedback");

    static initEventListener() {
        const nicknameForm = this.nicknameForm;

        if (nicknameForm === null) {
            return;
        }

        nicknameForm.addEventListener("blur", function () {
            const nickname = nicknameForm.value;
            if (NicknameValidator.isCorrectFormat(nickname)) {
                NicknameValidator.checkAvailability(nickname);
            }
        });
    }

    static checkFormat(nickname) {
        this.isCorrectFormat(nickname);
    }

    static isCorrectFormat(nickname) {
        let message;
        if (hasWhiteSpace(nickname)) {
            message = "공백은 사용할 수 없습니다.";
        } else if (nickname.length < MIN_NICKNAME_LENGTH) {
            message = MIN_NICKNAME_LENGTH + "자 이상 입력해 주세요.";
        } else if (nickname.length > MAX_NICKNAME_LENGTH) {
            message = MAX_NICKNAME_LENGTH + "자를 초과하여 입력할 수 없습니다.";
        } else {
            return true;
        }
        this.nicknameInvalidFeedback.innerText = message;
        enableInvalid(this.nicknameForm);
        return false;
    }

    static checkAvailability(nickname) {
        const nicknameForm = this.nicknameForm;
        const nicknameInvalidFeedback = this.nicknameInvalidFeedback;

        const xhr = new XMLHttpRequest();
        xhr.addEventListener("readystatechange", function () {
            if (this.readyState === 4) {
                switch (this.status) {
                    case 200:
                        const checkNicknameResult = JSON.parse(xhr.responseText);
                        if (checkNicknameResult.available) {
                            enableValid(nicknameForm);
                        } else {
                            const message = "'" + nickname + "' 은(는) 이미 다른 사용자가 사용중입니다.";
                            nicknameInvalidFeedback.innerText = message;
                            enableInvalid(nicknameForm);
                        }
                        return;
                    case 500:
                        const message = "서버에 오류가 발생하여 중복 확인에 실패하였습니다.";
                        nicknameInvalidFeedback.innerText = message;
                        enableInvalid(nicknameForm);
                        return;
                }
            }
        });
        xhr.open("GET", "/user/availability?nickname=" + nickname);
        xhr.send();
    }
}

export {NicknameValidator};