# :coffee:BeanContainer
>취향에 맞는 카페 추천 웹 사이트

<br>

## :bookmark_tabs:프로젝트 주제
정보 찾기가 어려운 개인 카페를 취향에 맞게 카테고리 별로 찾기 위한 사이트

<br>

## :calendar:제작 기간
* IDEATON 1차 : 2024.07.23. - 2024.07.29.
* HACKATHON 1차 : 2024.07.30. - 2024.08.14.
* IDEATON 2차 : 2024.08.16. - 2024.08.22.
* HACKATHON 2차 : 2024.08.23. - 2024.09.04.

<br>

## :wrench:기술 스택
### :gear:개발 IDE
>IntelliJ IDEA

### 🎨Front-end
>html, js, css, thymeleaf

### 🧑‍💻Back-end
>SpringBoot, Spring Security, JWT, MySQL, Docker

### :globe_with_meridians:형상 관리 및 배포 도구
>GitHub, AWS

<br>

## :pushpin:업무 분담
* **임영규**: 팀장, 지도 관리, 간단 리뷰
* **김지은**: 게시판, 이미지 공통 처리
* **장민진**: JWT 인증 회원, 관리자 관리, 마이 페이지
* **민차현**: 채팅 관련 기능 및 메시징 처리

<br>

## :clipboard:ERD 설계
<img src="/src/main/resources/static/readme/BeanContainer_ERD.png">


<br>

## :point_right:핵심기능
>Admin ( 게시글 관리 )
- 모든 게시글을 관리하며 삭제할 수 있다.

>Client ( 회원가입, 로그인, 회원정보 수정 / 탈퇴, 카페 검색, 나만의 지도 생성, 카페 간단 리뷰 작성, 카페 추천 게시글 작성, 만남 채팅방 )
1. 비회원
    - 지역별, 카테고리별로 카페를 검색할 수 있다.
    - 브랜드 카페를 제외하여 검색할 수 있다.
    - 메인에서 유저들의 공개된 지도를 조회할 수 있다.
    - 게시판에서 카페 추천 글을 조회하여 정보를 얻을 수 있다.
      - 최신순, 조회수순, 좋아요순, 댓글순으로 정렬하여 조회할 수 있다.

2. 회원
    - 네이버, 카카오 소셜 로그인을 할 수 있다.
    - 지역별, 카테고리별로 카페를 검색할 수 있다.
    - 브랜드 카페를 제외하여 검색할 수 있다.
    - 카페를 검색하여 간단 리뷰를 작성할 수 있다.
      - 평점 총 5점의 별점을 등록할 수 있다.
      - 카테고리를 선택하여 카페에 대한 정보를 간단히 등록할 수 있다.
      - 3줄의 간단 리뷰를 작성할 수 있다.
    - 게시판에서 카페 추천 글을 조회하여 정보를 얻을 수 있다.
        - 최신순, 조회수순, 좋아요순, 댓글순으로 정렬하여 조회할 수 있다.
    - 지도 관리에서 나만의 지도를 생성 및 조회할 수 있다.
      - 자주 가거나 좋아하는 카페들을 저장할 수 있다.
      - 나만의 지도에 등록된 카페들을 확인하고 리뷰 정보(평점, 카테고리, 간단 리뷰)를 확인 할 수 있다.
    - 게시판에서 카페 추천 게시글을 작성 / 수정 / 삭제를 할 수 있다.
      - 위지윅 에디터 도구를 이용하여 좀 더 편리하게 게시글을 작성할 수 있다. 
      - 최대 5장의 이미지를 첨부하여 카페에 대한 상세한 리뷰를 작성할 수 있다.
    - 게시글에 댓글을 작성할 수 있고, 좋아요를 누를 수 있다.
    - 회원 정보를 수정(프로필 이미지, 닉네임 변경)하거나 탈퇴할 수 있다.
    - 취향과 목적이 비슷한 지역 사람들과 소통하여 함께 카페를 방문하거나 카페 관련 활동을 즐길 수 있다.
   
   <br>

<details>
<summary><b>상세 설명 펼치기</b></summary>

### Main
<img src="/src/main/resources/static/readme/main.png">

* 전체 카페 검색
* 지역별 카페 검색
* 카테고리별 카페 검색
* 브랜드 카페 제외 검색
* 회원들의 공개된 지도 랜덤 조회

    <details>
      <summary><b>카테고리 검색 이미지 참고</b></summary>
      <img src="/src/main/resources/static/readme/main_category.png">
    </details>

* 로그인 / 회원가입

   <details>
      <summary><b>회원가입 이미지 참고</b></summary>
      <img src="/src/main/resources/static/readme/user_signup.png">
   </details>
  
    * 아이디 4글자 이상, 중복 불가
    * 비밀번호는 8자 이상 20자 이하이며 영문, 숫자, 특수 문자를 모두 포함
    * 비밀번호 확인
    * 닉네임 2자 이상 10자 이하
    * 이메일 인증
  
   <details>
      <summary><b>로그인 이미지 참고</b></summary>
      <img src="/src/main/resources/static/readme/login.png">
   </details>

    * 아이디, 비밀번호 입력
    * 소셜 로그인 (네이버, 카카오)

  
* 게시판 조회

### 내 정보 관리
<img src="/src/main/resources/static/readme/user_management.png">

* 프로필 이미지 업로드 및 제거
* 닉네임 변경
* 회원탈퇴

### 나만의 지도
<img src="/src/main/resources/static/readme/my_map.png">

* 카페 검색
* 카테고리별 검색
* 브랜드 카페 제외 검색
* 자주 가는 카페, 가고 싶은 카페 저장
* 지도 이름 생성
* 지도 공개 / 비공개
* 지도 목록 조회 및 관리

   <details>
      <summary><b>이미지 참고</b></summary>
      <img src="/src/main/resources/static/readme/map_management.png">
   </details>

### 게시판
<img src="/src/main/resources/static/readme/post_list.png">

* 최신순, 조회수순, 좋아요순, 댓글순 정렬
* 글쓰기
  * 위지윅 에디터 적용 및 이미지 첨부

     <details>
        <summary><b>이미지 참고</b></summary>
        <img src="/src/main/resources/static/readme/post_create.png">
     </details>


### 게시글

<img src="/src/main/resources/static/readme/post_details.jpeg">

* 작성자 - 수정, 삭제 가능
  * 이외 - only 목록 버튼
* 댓글 작성
  * 작성자 - 수정, 삭제 가능
  * 이외 - 버튼 비활성화
* 좋아요


### 만남 채팅방
* 채팅방 목록 조회
     <details>
        <summary><b>이미지 참고</b></summary>
        <img src="/src/main/resources/static/readme/chat_room_list.png">
     </details>
  
* 채팅방 방 제목, 인원 수 설정하여 개설
* 인원 수 초과 시 입장 불가
* 채팅
     <details>
        <summary><b>이미지 참고</b></summary>
        <img src="/src/main/resources/static/readme/chat_room.png">
     </details>

</details>

