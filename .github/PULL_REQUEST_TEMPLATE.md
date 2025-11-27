## 제목 : [태그] PR 제목

## 📝작업 내용

<!-- 구현한 기능에 대한 구체적인 내용을 작성해주세요 -->

- Member Entity 생성
- 회원가입 요청 처리 API 개발
  - DTO 생성 (요청용)
  - MemberController 및 MemberService 구현
- 회원가입 정상 작동 Test
- 중복된 이메일로 가입 시 예외 처리 Test

<br/>

## 👀변경 사항

<!-- 컴포넌트, API, 로직 등 코드 변경으로 인해 협업 시 다른 개발자가 주의해야 할 내용이 있다면 작성해주세요 -->

- 메인 개발 application.yml 에서 database 이름 변경했음
  - mysql에 database 이름 goojakgyo 새로 만들어서 쓰기
- Test 코드 작성을 위한 기본적인 설정 완료(application.yml)
  - mysql에 database 이름 goojakgyo_test 새로 만들어서 쓰기

<br/>

## #️⃣관련 이슈

<!-- 해당 PR과 관련된 이슈 번호가 있다면 "- #22" 형태로 작성해주세요 -->