# QnaBoard

> ** Q&A 게시판 프로젝트**
>
> demo : https://qnaboard.com



### 1. 제작기간 & 참여인원

<hr/>


- 2021-12-13 ~ 2022-01-07
- 개인 프로젝트



### 2. 사용기술

<hr/>


🔨**Backend**

| 기술명          | Version |
| --------------- | ------- |
| Springboot      | 2.6.1   |
| Gradle          | 7.3.1   |
| Spring Data JPA | 2.6.0   |
| QueryDSL        | 5.0.0   |
| H2              | 1.4.200 |
| MySQL           | 8.0.27  |
| Thymeleaf       |         |

**🎸 기타**

| 기술명  | Version |
| ------- | ------- |
| Jenkins | 2.325   |



### 3. 엔티티 및 ERD 설계

<hr/>

#### 3.1 👓기능 분석

**회원 기능**

- 회원가입 회원탈퇴, 로그인
- 내가 작성한 질문 및 답변 조회

**질문 기능**

- 질문 작성, 수정, 삭제
- 질문 조회
- 검색필터를 사용한 질문 검색
  - 글 제목. 검색태그, 답변존재여부, 질문작성일, 투표점수범위등을 활용한 검색
- 질문이 유용한지에 대한 투표 기능

**답변 기능**

- 답변 작성, 수정, 삭제
- 답변이 유용한지에 대한 투표 기능

**검색태그 기능**

- 질문게시판 관리자에 의한 검색태그 추가, 수정, 삭제
- 검색태그 조회

**대댓글 기능**

- 질문 및 답변에 댓글 작성
- 댓글 수정, 삭제
- 댓글에 댓글을 다는 '대댓글' 기능 구현



#### 3.2 🛠엔티티 설계

![](https://i.imgur.com/MxrEXji.png)

**회원(Member)**

- 이름과 역할(관리자, 일반사용자)을 가진다.

**질문(Question)**

- 질문글은 태그를 사용하는 검색을 지원하기 위해서 태그정보를 가져야 한다  
  하나의 질문글은 여러 태그를 가질 수 있으므로, 중간테이블인 QuestionTag와 일대다 관계를 맺는다.
- 질문글은 0개 이상의 답변글(answers)을 가질 수 있다.
  여러 답변이 달릴 수 있으니, Answer와 일대다 관계를 맺는다.
- 질문이 유용한지 여부를 나타내기 위해서 upvote, downvoteCount를 가진다

**질문글에 달린 태그(QuestionTag)**

- 질문글(Question)과 태그(Tag)는 다대다 관계를 가진다.
- @ManyToMany를 사용하기보다 OneToMany, ManyToOne으로 풀어서 표현하는 방법을 선택했다.

**태그(Tag)**

- 어떤 내용에 연관된 질문글만 검색할 수 있도록 태그를 추가했다.
- 태그는 작성자와 태그이름, 태그 색상을 가진다

**답변(Answer)**

- 질문글에 대한 답변에 해당하는 엔티티이다.
- 질문글처럼 upvote와 downvoteCount를 가진다. 답변이 유용한지를 판단할 수 있는 척도이다.
- 질문글을 올린 멤버가 답변글을 체택하는 경우 accepted의 값이 true가 된다



#### 3.3 🧰ERD 설계

![](https://i.imgur.com/SIyhe2i.png)

**싱글테이블 전략**

- 답변글(Answer)과 질문글(Question) 엔티티는 하나의 테이블(Post)로 관리하도록 설계했다
- 엔티티에서 사용하지 않는 컬럼에는 null을 허용해야 하고, 테이블이 지나치게 커질 수 있긴 하지만, insert할 때 하나의 테이블에만 insert하면 되고, 조회할때도 join없이 필요한 데이터를 가져올 수 있다는 장점이 있어서 싱글테이블 전략을 사용했다

**인덱스 적용**

- 나중에 성능최적화하면서 인덱스도 사용하면, 이 부분의 내용도 채우자



### 4. 핵심기능

<hr/>


#### 4.1 전체흐름

- 전체흐름을 표현하는 흐름도

#### 4.2 게시글 검색 및 페이지네이션

- 리액트 화면에서 검색조건 넣는 부분

#### 4.3 대댓글 기능

- 효율적으로 대댓글을 가져오기 위한 고민에 대해 작성



### 5. 핵심 기능을 위한 고민

<hr/>


#### 5.1 복잡한 동적쿼리와 페이징 처리

- 복잡한 동적쿼리를 QueryDSL과 Spring Data JPA를 가지고 어케 해결했는지 소개

#### 5.2 N + 1 문제

- N+1 문제가 뭔지 간단히 소개하고 어떻게 해결했는지 소개



### 6. 그 외의 고민

<hr/>

#### 6.1 상속을 통해 대댓글 기능 재활용

- 질문글과 답변글은 공통점이 많지만 다른 부분이 있어서 같은 엔티티로 처리할 수 없었다
- 어떻게 할까 고민하다가 두 객체의 공통된 부분을 추출해서 게시글(Post)이라는 엔티티를 만들고 상속을 사용해서 공통부분을 재활용할 수 있었다
- 특히 대댓글 기능을 게시글 클래스에서 구현하고 질문글과 답변글 엔티티에게 상속해줘서 중복구현 없이 기능을 재활용할 수 있었다

#### 6.? 메세지 기능

- 문자열 하드코딩 안하고 메세지 기능을 사용했음을 어필

#### 6.? 예외처리

- ControllerAdvice를 사용한 예외처리 부분이 전체적으로 어케 생겼는지 소개

#### 6.? 개발환경 구축

- 로컬, 개발서버, 운영서버마다 별도의 설정파일로 관리한다

  ```
  src/main/resources
            |--application.yml
            |--application-local.yml
            |--application-dev.yml
            |--application-prod.yml
  				
  ```

- 로컬환경의 MySQL 데이터베이스는 docker-compose를 사용해서 편하게 구축한다
  개발서버 및 운영서버의 데이터베이스는 AWS의 RDS를 사용해서 구축한다

- 스프링의 ddl-auto: create 설정은 로컬환경에서만 사용하도록 한다

### 7. 배포

<hr/>

#### 7.1 ⚙배포환경

![](https://i.imgur.com/doM8mIi.png)

- 로컬환경에서 도커를 설치하고, 그 안에 젠킨스를 설치하여 배포를 진행했다.
- 젠킨스에서 빌드 및 테스트를 하고 jar파일을 S3에 업로드 한 다음, CodeDeploy에 배포요청을 해서 대상 EC2 인스턴스에서 앱을 실행할 수 있도록 설정했다.
- 데이터베이스의 주소 및 계정정보와 같은 민감한 내용을 담고 있는 설정파일은 Git 리포지토리에 올라가지 않도록 하고, 젠킨스에서 없는 설정파일을 따로 주입해서 빌드하도록 설정했다.
- 배포가 끝나서 EC2에서 앱을 실행시킬 때, 어떤 프로필로 동작할지를 명시해주었다.





