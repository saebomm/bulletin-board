# 🧱 bulletin-board – 커뮤니티 게시판 서비스

> Spring Boot + MyBatis + Thymeleaf + Spring Security 기반의  
> 회원·게시글·댓글로 구성된 **전형적인 커뮤니티 게시판**을 구현하는 프로젝트입니다.  
> 로그인/권한/뷰 템플릿/쿼리까지, 실제 서비스 수준의 게시판 아키텍처를 경험하는 것이 목표입니다.

---

## 💡 시나리오

- 사용자는 회원가입/로그인을 통해 서비스에 참여한다.
- 로그인한 사용자는 게시글을 작성할 수 있다.
- 게시글은 **제목 / 내용 / 작성자 / 작성일 / 조회수** 정보를 가진다.
- 로그인한 사용자는 게시글에 댓글을 작성할 수 있다.
- **작성자 본인만** 자신의 게시글/댓글을 수정·삭제할 수 있다.
- 관리자는 모든 게시글/댓글에 대해 삭제 권한을 가진다.
- 비로그인 사용자는 **게시판 목록·상세 조회만 가능**하며, 작성/수정/삭제는 불가능하다.
- 화면은 **Thymeleaf 기반 SSR**로 렌더링되고, 데이터 접근은 **MyBatis**로 처리된다.
- 로그인·로그아웃·권한 체크는 **Spring Security 세션 기반 인증**으로 처리한다.

---

## 📋 요구사항

### 1) 회원(Member) 도메인
- 회원가입: 아이디/비밀번호/이름/이메일 입력, 아이디 중복 체크, 비밀번호 해시 저장
- 로그인/로그아웃: 세션 기반 인증
- 권한/역할: ROLE_USER / ROLE_ADMIN

### 2) 게시글(Article) 도메인
- 목록(페이징), 상세(조회수 증가)
- 작성(로그인 필요), 수정·삭제(작성자만)

### 3) 댓글(Comment) 도메인
- 댓글 목록
- 작성(로그인 필요)
- 수정·삭제(작성자 혹은 관리자)

### 4) 보안 요구사항
- Spring Security 기반 인증·인가
- PasswordEncoder 적용
- 작성자 체크 + ROLE_ADMIN 허용

---

## 🎯 체크리스트

1. Spring Boot 프로젝트 생성 (group/package 매핑)
2. MyBatis 의존성 및 기본 설정
3. Spring Security 기본 설정(WebSecurityConfig)
4. Member 엔티티/테이블 설계 및 Mapper/SQL 작성
5. 회원가입 화면 + 컨트롤러 + 서비스 + 암호화 로직 구현
6. 로그인/로그아웃 기능 + Security 연동
7. Article 엔티티/테이블 설계 및 Mapper/SQL 작성
8. 게시글 목록/상세/작성/수정/삭제 구현
9. Comment 엔티티/테이블 설계 및 Mapper/SQL 작성
10. 댓글 목록/작성/수정/삭제 구현
11. 작성자 본인 여부 기반 권한 체크
12. 관리자 전체 삭제 권한 적용
13. Thymeleaf 템플릿 정리
14. 기본 예외/에러 페이지 정리
15. 통합 테스트 및 브라우저 테스트

---

## 📂 폴더 구조

```bash
bulletin-board/
├─ docs/
│  └─ bulletin-board-erd.md
├─ gradle/
│  └─ wrapper/
│     ├─ gradle-wrapper.jar
│     └─ gradle-wrapper.properties
├─ src/
│  ├─ main/
│  │  ├─ java/com/saebom/bulletinboard/
│  │  │  ├─ config/        # 보안, 인터셉터, 웹 설정
│  │  │  ├─ controller/    # 웹 요청 처리
│  │  │  ├─ domain/        # 핵심 도메인 엔티티
│  │  │  ├─ dto/           # 요청/응답 DTO
│  │  │  │  ├─ article/
│  │  │  │  ├─ comment/
│  │  │  │  └─ member/
│  │  │  ├─ exception/     # 도메인/인증 예외
│  │  │  ├─ repository/    # MyBatis Mapper 인터페이스
│  │  │  ├─ service/       # 비즈니스 로직
│  │  │  ├─ session/       # 세션 상수 관리
│  │  │  ├─ validation/    # 커스텀 검증 로직
│  │  │  └─ web/           # 공통 웹 처리(@ControllerAdvice 등)
│  │  └─ resources/
│  │     ├─ application.yml
│  │     ├─ ddl/
│  │     ├─ mybatis/
│  │     │  ├─ mybatis-config.xml
│  │     │  └─ mapper/
│  │     ├─ static/
│  │     │  ├─ css/
│  │     │  └─ img/
│  │     └─ templates/
│  │        ├─ articles/
│  │        └─ member/
│  └─ test/
│     └─ java/com/saebom/bulletinboard/
│        └─ repository/
├─ build.gradle
├─ settings.gradle
├─ gradlew
└─ gradlew.bat
```