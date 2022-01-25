const toggleCommentWriter = (event) => {
    event.preventDefault();
    const comment = event.target.closest(".comment");
    const commentWriter = comment.querySelector(".comment-writer");
    commentWriter.classList.toggle("d-none")
};

const toggleCommentEditor = (event) => {
    event.preventDefault();
    const comment = event.target.closest(".comment");
    const commentWriter = comment.querySelector(".comment-editor");
    commentWriter.classList.toggle("d-none")
};

const createComment = async (event) => {
    event.preventDefault();
    //필요한 엘리먼트 찾기
    const commentTextArea = event.target[0];
    const content = commentTextArea.value;
    const commentWrapper = commentTextArea.closest(".comment-wrapper");
    const commentList = commentWrapper.querySelector(".comment-list");
    //게시글 아이디 찾기
    const postId = commentTextArea.closest(".post").dataset.postid;
    //api url 생성
    const url = `/posts/${postId}/comments`;
    //서버에 전송할 body 생성
    let body = {
        "parentCommentId": null,
        "content": content
    };
    try {
        //api 요청
        const response = await request(url, PUT, body);
        //엘리먼트 생성을 위해 dom parser생성
        const domParser = new DOMParser();
        //domParser를 가지고 응답메세지를 엘리먼트로 파싱
        const comment = domParser.parseFromString(response, "text/html").querySelector(".comment");
        //이벤트리스너 바인딩
        setCommentEventListener(comment);
        //엘리먼트를 dom에 추가
        commentList.appendChild(comment);
        commentTextArea.value = "";

    } catch (error) {
        alertError(error);
    }
};

const replyComment = async (event) => {
    event.preventDefault();
    const commentTextArea = event.target[0];
    const commentWriter = event.target.closest(".comment-writer");
    const content = commentTextArea.value;
    const parentComment = event.target.closest(".comment");
    const parentCommentId = parentComment.id.substring(2);
    const commentList = parentComment.querySelector(".child-comment-wrapper");

    const postId = commentTextArea.closest(".post").dataset.postid;

    const url = `/posts/${postId}/comments`;

    let body = {
        "parentCommentId": parentCommentId,
        "content": content
    };

    try {
        const response = await request(url, PUT, body);
        const domParser = new DOMParser();
        const comment = domParser.parseFromString(response, "text/html").querySelector(".comment");
        setCommentEventListener(comment);
        commentList.appendChild(comment);
        commentTextArea.value = "";
        commentWriter.classList.toggle("d-none");
    } catch (error) {
        alertError(error);
    }
};

const deleteComment = async (event) => {
    const comment = event.target.closest(".comment");
    const commentId = comment.id.substr(2);
    const postId = comment.closest(".post").dataset.postid;

    const url = `/posts/${postId}/comments/${commentId}`;
    try {
        const response = await request(url, DELETE, null);
        comment.querySelector(".comment-author").innerText = response.deletedAuthorName;
        comment.querySelector(".comment-content").innerText = response.deletedContentName;
    } catch (error) {
        alertError(error);
    }
}

const editComment = async (event) => {
    event.preventDefault();
    //textarea 찾고 안의 내용 꺼내기
    const commentTextArea = event.target[0];
    const content = commentTextArea.value;
    //wrapper 태그 찾고 거기 들어있는 postId꺼내기
    const postId = commentTextArea.closest(".post").dataset.postid;
    //댓글 컴포넌트 찾고 아이디 꺼내기
    const comment = event.target.closest(".comment");
    const commentId = comment.id.substr(2);
    //댓글 수정 폼을 감싸고 있는 태그 찾기
    const commentEditor = event.target.closest(".comment-editor");
    //이전 content 값을 가지고 있는 태그와 이전값을 꺼내기
    const prevContentContainer = commentEditor.querySelector(".prev-comment-content");
    const prevContent = prevContentContainer.value;

    //바뀐게 없다면 API 호출 안함
    if (prevContent === content) {
        //댓글 수정 폼 숨기기
        commentEditor.classList.toggle("d-none");
        return;
    }

    const url = `/posts/${postId}/comments/${commentId}`;

    let body = {
        "content": content
    };

    try {
        const response = await request(url, PATCH, body);

        //댓글 내용 바꾸고 입력폼의 내용도 최신화하기
        comment.querySelector(".comment-content").innerText = response.content;
        commentTextArea.value = response.content;
        prevContentContainer.value = response.content;
        //댓글 수정 폼 숨기기
        commentEditor.classList.toggle("d-none");
    } catch (error) {
        alertError(error);
    }
};


const setCommentEventListener = (comment) => {
    comment.querySelector(".toggle-comment-writer").addEventListener("click", toggleCommentWriter);
    comment.querySelector(".reply-comment-form").addEventListener("submit", replyComment);
    comment.querySelector(".toggle-comment-editor").addEventListener("click", toggleCommentEditor);
    comment.querySelector(".edit-comment-form").addEventListener("submit", editComment);
    comment.querySelector(".delete-comment-button").addEventListener("click", deleteComment);
}
