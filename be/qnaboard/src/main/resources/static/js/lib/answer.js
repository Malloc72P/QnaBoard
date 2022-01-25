const createAnswer = async (event) => {
    event.preventDefault();
    const textAreaElement = document.getElementById("textarea-submit-answer");
    const content = textAreaElement.value;
    const questionId = textAreaElement.closest(".content-root").dataset.questionid;
    const url = `/questions/${questionId}/answers`;
    const body = {"content": content};

    try {
        //필요한 엘리먼트 찾기
        const response = await request(url, POST, body);
        const parser = new DOMParser();
        const answerElementWrapper = parser.parseFromString(response, "text/html");
        const answerElement = answerElementWrapper.querySelector(".answer");
        //새로 생성된 엘리먼트에 이벤트리스너 바인딩
        setAnswerEventListener(answerElement);
        //엘리먼트를 dom에 추가
        appendAnswer(answerElement);
        //답변게시글 입력 필드 초기화
        textAreaElement.value = "";
    } catch (error) {
        alertError(error);
    }

};

const appendAnswer = (answerElement) => {
    //답변게시글 목록에 새로 생성한 답변게시글 추가
    const answerWrapper = document.getElementById("answer-wrapper");
    answerWrapper.appendChild(answerElement);
    //답변게시글 개수 증가
    increaseAnswerCount();
};

const setAnswerEventListener = (answerElement) => {
    answerElement.querySelector(".answer-delete").addEventListener("click", deleteAnswer);
    answerElement.querySelector(".answer-edit").addEventListener("click", toggleEditAnswerForm);
    answerElement.querySelector(".answer-edit-form").addEventListener("submit", editAnswer);
    answerElement.querySelector(".up-vote-button").addEventListener("click", vote);
    answerElement.querySelector(".down-vote-button").addEventListener("click", vote);
    answerElement.querySelector(".create-comment-form").addEventListener("submit", createComment);
};

const deleteAnswer = async (event) => {
    const section = event.target.closest("section");
    const questionId = section.querySelector(".question").id.substring(2);
    const answer = event.target.closest(".answer");
    const answerId = answer.id.substring(2);

    const url = `/questions/${questionId}/answers/${answerId}`;

    let body = {};

    try {
        await request(url, DELETE, body);
        answer.remove();
        decreaseAnswerCount();

    } catch (error) {
        alertError(error);
    }

};

const editAnswer = async (event) => {
    event.preventDefault();
    const section = event.target.closest("section");
    const questionId = section.querySelector(".question").id.substring(2);
    const answer = event.target.closest(".answer");
    const answerId = answer.id.substring(2);
    const content = answer.querySelector(".answer-edit-form")[0].value;

    const url = `/questions/${questionId}/answers/${answerId}`;

    let body = {
        "content": content
    };

    try {
        const response = await request(url, PATCH, body);
        const answerContent = answer.querySelector(".post-content-wrapper");
        answerContent.innerText = response.content;

        const lastModifiedDate = answer.querySelector(".post-controller .last-modified-date");
        lastModifiedDate.innerText = response.lastModifiedDate;

        // answer.querySelector(".answer-edit").dispatchEvent(new Event("click"));
        answer.querySelector(".answer-edit-form").classList.toggle("d-none");
    } catch (error) {
        alertError(error);
    }
};

const toggleEditAnswerForm = (event) => {
    event.target
        .closest(".answer")
        .querySelector(".answer-edit-form")
        .classList
        .toggle("d-none");
};

const increaseAnswerCount = () => {
    const answerCount = document.getElementById("answer-count");
    answerCount.innerText = parseInt(answerCount.innerText) + 1;
}

const decreaseAnswerCount = () => {
    const answerCount = document.getElementById("answer-count");
    answerCount.innerText = parseInt(answerCount.innerText) - 1;
}
