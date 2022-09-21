# bluesky 프로젝트 소개 페이지에 오신 것을 환영합니다.

## 개요
bluesky 는 다양한 분야의 커뮤니티를 가지고 있는 통합 커뮤니티 사이트입니다.  
사용자는 자신이 원하는 커뮤니티에 게시글과 댓글을 남겨  
해당 분야에 관심이 있는사람들과 교류 활동을 할 수 있습니다.  
반응형 웹으로 제작되어 PC와 모바일 어느 쪽에서든 편리하게 이용할 수 있습니다.  
[사이트 바로가기](https://vluesky.herokuapp.com)

## 0. 목차
1. [주요 기능](#1-주요-기능)
- [회원가입](#회원가입)
- [로그인](#로그인)
- [글쓰기 & 글 수정하기](#글쓰기--글-수정하기)
- [댓글](#댓글)
- [검색](#검색)
- [좋아요 기능](#좋아요-기능)
2. [사용 기술](#2-사용-기술)
3. [Entity Diagram](#3-entity-diagram)
4. [Schema Diagram](#4-schema-diagram)

## 1. 주요 기능
### 회원가입
사용자의 이메일로 인증 메일을 전송하여, 인증 메일의 링크를 통해 회원가입을 진행합니다.
1. 인증메일 전송  
   <br>
   <img width="300" src="https://user-images.githubusercontent.com/65878323/190340452-ee2b3082-1b92-46c5-b04b-2e78dbb89323.gif" alt="회원가입">  
   <img width="900" src="https://user-images.githubusercontent.com/65878323/190339781-37982bba-ecd1-4c97-9c92-87b9532a6b58.PNG" alt="회원가입">  
   <br>
   <br>
2. 가입 정보 입력 및 가입 완료   
   <br>
   <img width="300" src="https://user-images.githubusercontent.com/65878323/190339508-2a3498c3-8ca5-47b7-baeb-4e63ae3ea5b8.gif" alt="회원가입">  
   <br>

---

### 로그인
이메일 로그인과 소셜 로그인이 가능합니다.  
로그인 후에 원래 보고 있던 페이지로 리다이렉트 됩니다.
<br>
<br>
<img width="300" src="https://user-images.githubusercontent.com/65878323/190342737-0393a840-8d05-430c-b243-300426a7d550.gif" alt="로그인">  
<br>

---

### 글쓰기 & 글 수정하기
해당 게시판에 속한 카테고리 중 하나를 선택 할 수 있습니다.  
에디터의 버튼을 조작하여 글자 크기 등을 변경할 수 있습니다.
1. 글 쓰기  
   <br>
   <img width="300" src="https://user-images.githubusercontent.com/65878323/190406928-2626e1bb-16a7-41a3-b5dc-dd36fb6ca2bc.gif" alt="글쓰기">  
   <br>
   <br>
2. 글 수정하기  
   <br>
   <img width="300" src="https://user-images.githubusercontent.com/65878323/190571143-eb5daf42-d4f1-4078-86ca-e34c5f3a9259.gif" alt="글 수정하기">  
   <br>

---

### 댓글
1. 댓글 & 대댓글 쓰기  
* 대댓글의 경우에는 부모 댓글의 작성자명이 댓글에 추가됩니다.  
   <br>
   <img width="300" src="https://user-images.githubusercontent.com/65878323/190588339-3f909e10-f795-454f-8b41-e8f0d9b1eac2.gif" alt="댓글 쓰기">  
   <br>
   <br>
2. 페이징  
   한 페이지에 최대 5개의 댓글이 보여지도록 하되,  
   해당 댓글들에 달린 답글은 개수와 상관없이 전부 보여지도록 구성하였습니다.  
   <br>
   <img width="300" src="https://user-images.githubusercontent.com/65878323/190573449-c8fc7767-b525-4973-9e1b-b3cab63b7d5a.gif" alt="댓글 페이징">  
   <br>
   <br>
3. 댓글 수정  
   <br>
   <img width="300" src="https://user-images.githubusercontent.com/65878323/190590065-6f6b07aa-9690-474f-bb1a-4fbadc789eb5.gif" alt="댓글 수정">  
   <br>
   <br>
4. 댓글 삭제  
   자신을 부모로 삼는 하위 댓글이 없다면, 삭제한 부모 댓글은 화면에 노출되지 않지만,  
   존재한다면 댓글 삭제 후에도 화면에 노출이 되게 하였습니다.  
   <br>
   (1번 댓글의 답글이 2번, 2번의 답글이 3번인 경우)  
   <img width="300" src="https://user-images.githubusercontent.com/65878323/190847315-8fd842c3-def9-4ea7-9fe3-174f258ef23e.gif" alt="댓글 삭제">  
   <br>

---

### 검색
* 헤더에 있는 검색 메뉴를 이용하면 사이트 내 전체 게시글을 검색할 수 있습니다.
* 게시판 내에 있는 검색 메뉴에서는 해당 게시판 내의 게시글만 검색할 수 있습니다.
* 검색 결과에 있는 검색 키워드에 강조 표시가 되어집니다.

1. 게시글 검색  
   <br>
   <img width="300" src="https://user-images.githubusercontent.com/65878323/190856441-50d036c3-485d-49d5-beb9-a0569ce00645.gif" alt="게시글 검색">  
   <br>
   <br>

2. 댓글 검색  
* 검색된 댓글과 댓글이 속한 게시글이 같이 표시됩니다.  
<br>
<img width="300" src="https://user-images.githubusercontent.com/65878323/190856677-8cf4703d-85ed-439c-a40a-1a5f1bcca656.gif" alt="댓글 검색">  
<br>
<br>

3. 게시판 내 게시글 검색  
* 카테고리 선택 후 검색을 하면 해당 카테고리의 게시글만 검색 됩니다.  
<br>
<img width="300" src="https://user-images.githubusercontent.com/65878323/190857058-4564a473-4e11-4fab-bf01-5170d1f07600.gif" alt="게시판 내 게시글 검색">  
<br>
<br>

4. 게시판 내 댓글 검색  
* 카테고리 선택 후 검색을 하면 해당 카테고리의 댓글만 검색 됩니다.  
   <br>
   <img width="300" src="https://user-images.githubusercontent.com/65878323/190889999-0507d6e4-1060-41e8-8d5e-454850d41867.gif" alt="게시판 내 댓글 검색">  
   <br>
   <br>

---

### 좋아요 기능
마음에 드는 게시글 및 댓글에 좋아요를 할 수 있습니다.  
<br>
<img width="300" src="https://user-images.githubusercontent.com/65878323/190988061-c8bb6b4f-a74c-4624-8a1d-c30fb7fad532.gif" alt="좋아요 기능">
<br>
<br>

## 2. 사용 기술
### Deploy
- heroku

### Backend
- Java 11
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL 8.0
- Querydsl

### Frontend
- Bootstrap 5
- JavaScript ES6
- HTML5
- CSS3

## 3. Entity Diagram
<img width="800" src="https://user-images.githubusercontent.com/65878323/189076624-eaa40a59-b9a9-4cf6-af14-ad2446eccd75.png" alt="엔티티 다이어그램">

## 4. Schema Diagram
<img width="800" src="https://user-images.githubusercontent.com/65878323/189076751-ec031436-fca1-4a77-aeb6-dd39089a9066.png" alt="스키마 다이어그램">