# QnaBoard

> ** Q&A 게시판 프로젝트**
>
> demo : https://qnaboard.com



### 1. 제작기간 & 참여인원

<hr>

- 2021-12-13 ~ 2021-12-24
- 개인 프로젝트



### 2. 사용기술

<hr>

🔨**Backend**

| Springboot      | Version |
| --------------- | ------- |
| Springboot      | 2.6.1   |
| Gradle          |         |
| Spring Data JPA | 2.6.0   |
| QueryDSL        |         |
| H2              | 1.4.200 |
| MySQL           | 8       |

🪓**Frontend**

| Springboot | Version |
| ---------- | ------- |
| ReactJS    | 17.0.2  |



### 3. 엔티티 및 ERD 설계

<hr>

#### 3.1 엔티티 설계

![](https://i.imgur.com/lR6MZW4.png)

- **회원(Member)**
  - 이름과 역할(관리자, 일반사용자)을 가진다
- **질문(Question)**
  - 질문글은 태그를 사용하는 검색을 지원하기 위해서 태그정보를 가져야 한다
    하나의 질문글은 여러개의 태그를 가질 수 있으므로 중간테이블인 QuestionTag와 일대다 관계를 맺는다.
  - 질문글은 0개 이상의 답변글(answers)을 가지고 있다. 
    여러개의 답변이 달릴 수 있으니 Answer와 일대다 관계를 맺는다
  - 질문이 유용한지 여부를 나타내기 위해서 upvote, downvoteCount를 추가했다
- **질문글에 달린 태그(QuestionTag)**
  - 질문글(Question)과 태그(Tag)는 다대다 관계를 가진다
    이를 표현하기 위해 @ManyToMany를 사용하기보다 OneToMany, ManyToOne으로 풀어서 표현하는 
    방법을 선택했다

- **태그**
  - 어떤 내용에 연관된 질문글만 검색할 수 있도록 태그를 추가했다.
  - 태그는 작성자와 태그이름, 태그 색상을 가진다
- **답변(Answer)**
  - 질문글에 대한 답변에 해당하는 엔티티이다.
  - 질문글처럼 upvote와 downvoteCount를 가진다. 답변이 유용한지를 판단할 수 있는 척도이다.
  - 질문글을 올린 멤버가 답변글을 체택하는 경우 accepted의 값이 true가 된다

#### 3.2 ERD 설계

- 이미지



### 4. 핵심기능

<hr>

#### 4.1 전체흐름

- 전체흐름을 표현하는 흐름도

#### 4.2 게시글 검색 및 페이지네이션

- 리액트 화면에서 검색조건 넣는 부분



### 5. 핵심 트러블 슈팅

<hr>

#### 5.1 복잡한 동적쿼리와 페이징 처리

- 복잡한 동적쿼리를 QueryDSL과 Spring Data JPA를 가지고 어케 해결했는지 소개

#### 5.2 N + 1 문제

- N+1 문제가 뭔지 간단히 소개하고 어떻게 해결했는지 소개

#### 5.3 메세지 기능

- 문자열 하드코딩 안하고 메세지 기능을 사용했음을 어필

#### 5.4 예외처리

- ControllerAdvice를 사용한 예외처리 부분이 전체적으로 어케 생겼는지 소개