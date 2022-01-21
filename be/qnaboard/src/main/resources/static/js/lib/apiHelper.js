const GET = "GET";
const POST = "POST";
const PATCH = "PATCH";
const PUT = "PUT";
const DELETE = "DELETE";

const commonHeader = {
    "Content-Type": "application/json",
    "Accept": "*/*",
    "Accept-Encoding": "gzip, deflate, br",
};

/**
 * API 요청을 단순화하기 위한 함수
 * @param uri 요청 URL
 * @param method 요청메서드
 * @param body 메세지 바디
 * @param responseType 응답 타입(html은 MODE_TEXT, json은 MODE_JSON)
 * @returns {Promise<string|any>} 프로미스, await하면 메세지 바디가 text 또는 json으로 파싱되어 반환된다
 */
const request = async (uri, method, body) => {
    let requestMessage = {
        method: method,
        headers: commonHeader
    };
    if (body !== null) {
        requestMessage["body"] = JSON.stringify(body);
    }

    let response = await fetch(serverAddress + uri, requestMessage);
    let content = null;

    switch (response.headers.get("content-type")) {
        case "application/json":
            content = await response.json();
            break;
        case "text/html;charset=UTF-8":
            content = await response.text();
            break;
    }

    if (!response.ok) {
        throw content;
    }

    return content;
};

const alertError = (error) => {
    if (error != null && error.description != null && error.description !== "") {
        alert(error.description);
    } else {
        alert("unknown error");
    }

};
