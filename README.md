# 중고거래 플랫폼

지역 기반 중고거래 플랫폼의 백엔드 API 서버로, JWT 인증, 실시간 채팅, 소셜 로그인 등을 구현하고 AWS에 배포한 개인 프로젝트입니다.

---

## 기술 스택

**Backend**

![Java](https://img.shields.io/badge/Java_17-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5.7-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security_6.x-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

**Database**

![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![H2](https://img.shields.io/badge/H2-1021FF?style=for-the-badge&logo=h2&logoColor=white)

**Infra**

![AWS EC2](https://img.shields.io/badge/AWS_EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)
![AWS RDS](https://img.shields.io/badge/AWS_RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white)
![AWS S3](https://img.shields.io/badge/AWS_S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)

**Frontend**

![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap_5-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white)

**Communication**

![WebSocket](https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=websocket&logoColor=white)
![STOMP](https://img.shields.io/badge/STOMP-010101?style=for-the-badge)

**Test & Docs**

![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-78A641?style=for-the-badge)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)

---

## 시스템 아키텍처

<img width="601" height="391" alt="시스템 아키텍처 drawio" src="https://github.com/user-attachments/assets/f8d89cf6-6f23-4731-8368-c75411a9ecad" />

---

## ERD

<img width="2080" height="1362" alt="ERD다이어그램" src="https://github.com/user-attachments/assets/b1bd3982-eb37-4914-9423-d77f88f37eb9" />

---

## 주요 기능

**인증/보안**
- JWT 기반 로그인 / 회원가입 (Access Token + Refresh Token HttpOnly 쿠키)
- OAuth2 소셜 로그인 (Google, Naver)
- 이메일 인증 기반 아이디 찾기 / 비밀번호 재설정
- 회원 탈퇴 (Soft Delete + Redis JWT 블랙리스트)

**게시글**
- 게시글 CRUD + 이미지 업로드 (S3)
- 동적 검색 (키워드, 가격범위, 지역, 판매상태) + 페이지네이션
- 판매상태 관리 (판매중 / 예약중 / 판매완료)
- 찜하기

**채팅**
- WebSocket + STOMP 기반 실시간 1:1 채팅
- Redis 캐싱으로 최근 메시지 조회 성능 개선
- StompHandler를 통한 WebSocket 연결 시 JWT 인증

**댓글**
- 자기참조 구조를 활용한 댓글 / 대댓글
- Soft Delete 적용

**관리자**
- 유저 관리 (강제 탈퇴, 활동 정지)
- 게시글 관리 (강제 삭제)
- 통계 (가입자 수, 게시글 수, 정지된 유저 수)
<img width="1343" height="918" alt="로그인 페이지" src="https://github.com/user-attachments/assets/7ae20135-54d5-4e94-9bf8-a2b50d1fabe0" />
<img width="1495" height="924" alt="게시글 목록" src="https://github.com/user-attachments/assets/2b9a15e3-cb7a-4c0d-bd7c-5237578d46c2" />
<img width="1395" height="888" alt="게시글 상세" src="https://github.com/user-attachments/assets/298dec5a-4925-4d8d-a635-63ec41601522" />
<img width="1770" height="918" alt="채팅 화면" src="https://github.com/user-attachments/assets/235d988b-5b38-4d27-b27d-3510dba178f8" />
<img width="1568" height="768" alt="관리자 페이지" src="https://github.com/user-attachments/assets/114682d1-7b6a-4ef4-b2a7-1109d9e91960" />
<img width="1656" height="924" alt="스웨거" src="https://github.com/user-attachments/assets/9f9a0403-90e6-4b8c-bd63-2b22f59435a7" />
---

## 트러블슈팅

### 1. 게시글 목록 조회 시 N+1 문제 해결

**문제**
게시글 목록을 조회할 때 게시글마다 작성자 정보를 불러오기 위한 쿼리가 추가로 발생. 게시글이 만약 10개라면 작성자 조회 쿼리가 10번 더 나가는 N+1문제가 발생함

**시도**
Fetch Join으로 해결하려 했으나, Pageable과 함께 쓰면 JPA가 전체 데이터를 메모리에 올린 후 애플리케이션에서 페이징을 해버리는 문제가 생김

**해결**
@EntityGraph를 사용하여 연관 엔티티를 조회하면서도 DB 레벨 페이징이 정상으로 동작. 기존 Repository에 어노테이션만 추가하면 돼서 적용이 간단함

### 2. JWT 환경에서 회원 탈퇴 시 토큰 즉시 무효화

**문제**
JWT 기반 인증에서 회원 탈퇴 기능을 구현하려 했으나 JWT는 Stateless 방식이라 서버에서 특정 토큰을 강제로 무효화할 방법이 없음. 탈퇴 처리를 해도 발급된 엑세스 토큰이 만료 시간까지 유효하게 남는 문제가 생김

**시도**
엑세스 토큰 만료 시간을 짧게 설정하면 서버 부하가 늘어나고, DB에 블랙리스트 테이블을 만들면 모든 API요청마다 DB조회가 추가되어 성능부담이 큼

**해결**
레디스에 블랙리스트를 저장. 메모리 기반이라 조회가 빠르고, TTL을 토큰 남은 만료 시간으로 설정하면 만료 후 자동삭제되어 별도 정리 작업이 불필요. 프로젝트에 이미 레디스를 사용하고 있어서 추가 인프라 없이 적용 가능

### 3. 채팅 메시지 조회 시 DB 부하 개선

**문제**
채팅방에 입장할 때마다 MySQL에서 메시지를 조회해야 했다. 채팅은 방 입장이 빈번하게 일어나는 기능이라, 매번 DB 쿼리를 실행하면 사용자가 늘어날수록 DB에 부하가 집중되는 구조였다.

**시도**
로컬 캐시(Ehcache 등)는 빠르지만 서버가 여러 대일 때 각 서버마다 캐시가 따로 존재해서 데이터 정합성 문제가 생김

**해결**
레디스를 캐시 계층으로 도입. 채팅방별로 최근 50개 메시지를 레디스 List에 캐싱하여 입장 시 MySQL 대신 레디스를 먼저 조회하는 구조로 변경. 전체 메시지는 MySQL에 영구 보관하여 캐시 계층과 영속 계층을 분리

### 4. CI/CD 파이프라인에서 JAR 전송 실패

**문제**
깃허브 액션에서 빌드한 jar파일(약 79mb)을 scp로 ec2에 직접 전송하려고 시도. 하지만 네트워크 속도 문제로 전송이 타임아웃 되거나 실패하는 상황이 반복됨

**시도**
s3를 경유하여 jar를 업로드/다운로드 하는 방식으로는 전송 안전성은 해결됐지만, ec2에 java를 직접 설치해야하고 서버 환경 차이로 인한 문제가 남아있었음

**해결**
도커 이미지 방식으로 전환. 깃허브액션에서 도커 이미지 빌드 -> 도커 허브 push -> ec2에서 pull -> 도커 compose로 실행하는 파이프라인을 구축하여, main브랜치에 merge하면 자동으로 배포되는 ci/cd 환경을 완성

---

## 프로젝트 구조

```
src/main/java/com/example/marketproject/
├── config/          # Security, WebSocket, Swagger 설정
├── controller/      # REST API 컨트롤러
├── service/         # 비즈니스 로직
├── repository/      # JPA Repository
├── entity/          # JPA 엔티티
├── dto/             # 요청/응답 DTO
├── security/        # JWT, OAuth2 관련
└── util/            # 유틸리티 (DataInitializer 등)
```
