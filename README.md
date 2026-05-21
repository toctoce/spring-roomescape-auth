# 방탈출 인증과 인가

## 설명

인증/인가에 대해 배운다. `ArgumentResolver`를 사용해서 로그인 사용자를 컨트롤러 파라미터로 전달한다.

[기존 예약 서비스](https://github.com/toctoce/spring-roomescape-member)는 예약자 이름을 요청으로 직접 받아 사용자를 구분했다. 이번 미션에서는 웹과 모바일 요청에서 로그인한 사용자를 식별하고, 로그인 사용자를 기준으로 예약을 관리한다.

---

## 1단계: 웹에서 로그인하기

### 요구 사항

#### 로그인

- [x] 사용자는 로그인할 수 있다.
- [x] 로그인에 성공하면 이후 요청에서 같은 사용자를 식별할 수 있어야 한다.
- [x] 로그인에 실패하면 적절한 응답을 반환한다.
- [x] 사용자는 로그아웃할 수 있다.
- [x] 로그아웃한 사용자는 이후 인증이 필요한 기능을 사용할 수 없다.

#### 예약 생성

- [x] 로그인한 사용자는 예약을 생성할 수 있다.
- [x] 예약 생성 시 요청으로 받은 이름이 아니라 로그인한 사용자를 기준으로 예약을 만든다.
- [x] 로그인하지 않은 사용자는 예약을 생성할 수 없다.

#### 예약 조회

- [x] 로그인한 사용자는 자신의 예약을 조회할 수 있다.
- [x] 로그인하지 않은 사용자는 인증이 필요한 예약 조회 기능을 사용할 수 없다.

#### 인증 공통 처리

- [x] 로그인 여부 확인 로직을 컨트롤러마다 반복하지 않는다.
- [x] 인증이 필요한 API와 필요하지 않은 API를 구분한다.
- [x] 인증 실패 시 일관된 응답을 반환한다.

### API 명세

1단계에서 새로 추가하거나 변경한 API만 작성한다.

| 구분 | 기능 | Method | URL | 인증 | 설명 | Request Body | 성공 응답 | 실패 응답 |
|----|----|----|----|----|----|----|----|----|
| 추가 | 회원가입 | `POST` | `/members` | 불필요 | 로그인에 사용할 회원 생성 | `name`, `email`, `password` | `201 Created`, 회원 정보 | `400 Bad Request`, `409 Conflict` |
| 추가 | 웹 로그인 | `POST` | `/login` | 불필요 | 세션에 로그인 회원 id 저장 | `email`, `password` | `200 OK`, 회원 정보, `JSESSIONID` 쿠키 | `401 Unauthorized` |
| 추가 | 로그아웃 | `POST` | `/logout` | 필요 | 세션 무효화 | 없음 | `204 No Content` | `401 Unauthorized` |
| 추가 | 내 정보 조회 | `GET` | `/members/me` | 필요 | 로그인 회원 정보 조회 | 없음 | `200 OK`, 회원 정보 | `401 Unauthorized` |
| 변경 | 예약 생성 | `POST` | `/reservations` | 필요 | 로그인 회원 기준 예약 생성 | `date`, `timeId`, `themeId` | `201 Created`, 예약 정보 | `400 Bad Request`, `401 Unauthorized`, `409 Conflict`, `422 Unprocessable Entity` |
| 변경 | 내 예약 조회 | `GET` | `/reservations` | 필요 | 로그인 회원의 예약 목록 조회 | 없음 | `200 OK`, 예약 목록 | `401 Unauthorized` |
| 변경 | 예약 취소 | `DELETE` | `/reservations/{id}` | 필요 | 로그인 회원의 예약 취소 | 없음 | `204 No Content` | `401 Unauthorized`, `403 Forbidden`, `404 Not Found` |
| 변경 | 예약 수정 | `PATCH` | `/reservations/{id}` | 필요 | 로그인 회원의 예약 수정 | `date`, `timeId`, `themeId` | `200 OK`, 예약 정보 | `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found` |

### 요청/응답 예시

| 기능 | 예시 |
|----|----|
| 회원가입 요청 | `{ "name": "사용자", "email": "member@example.com", "password": "password" }` |
| 회원가입 응답 | `{ "id": 1, "name": "사용자", "email": "member@example.com" }` |
| 웹 로그인 요청 | `{ "email": "member@example.com", "password": "password" }` |
| 웹 로그인 응답 | `{ "id": 1, "name": "사용자", "email": "member@example.com" }`, `JSESSIONID` 쿠키 |
| 예약 생성 요청 | `{ "date": "2026-05-20", "timeId": 1, "themeId": 1 }` |
| 예약 조회 요청 | `GET /reservations` |
| 예약 취소 요청 | `DELETE /reservations/1` |
| 예약 수정 요청 | `PATCH /reservations/1` |
| 예약 응답 | `{ "id": 1, "memberId": 1, "date": "2026-05-20", "time": { ... }, "theme": { ... } }` |
| 인증 실패 응답 | `{ "message": "로그인이 필요합니다." }` |

### 상세 설명

#### 로그인 상태 유지 방식

- 웹은 세션 사용
- 로그인 성공 시 `HttpSession`에 로그인 회원 id 저장
- 세션 키: `loginMemberId`
- 브라우저는 이후 요청마다 `JSESSIONID` 쿠키 전송
- 서버는 세션의 `loginMemberId`로 로그인 사용자 식별
- 로그아웃 시 세션 무효화

#### 인증이 필요한 API 구분

인증이 필요한 API는 컨트롤러 메서드의 `@LoginMember` 파라미터로 구분한다.

인증이 필요한 API:

- `POST /reservations`
- `GET /reservations`
- `DELETE /reservations/{id}`
- `PATCH /reservations/{id}`
- `GET /members/me`
- `POST /logout`

인증이 필요하지 않은 API:

- `POST /members`
- `POST /login`
- 정적 리소스
- 예약 시간 조회
- 테마 조회

#### 인증 공통 처리 위치

- `ArgumentResolver` 사용
- 컨트롤러 파라미터에 `@LoginMember Member member` 선언
- `LoginMemberArgumentResolver`가 세션에서 `loginMemberId` 조회
- 세션이 없거나 `loginMemberId`가 없으면 `401 Unauthorized`
- 컨트롤러는 세션을 직접 다루지 않음

```java
@PostMapping("/reservations")
public ReservationResponse create(
        @LoginMember Member member,
        @RequestBody ReservationRequest request
) {
    Reservation reservation = reservationService.save(member.getId(), request);
    return ReservationResponse.from(reservation);
}
```

#### 예약 처리 방식

- 예약 생성 요청에서 예약자 이름을 받지 않음
- 예약 생성 요청에서 회원 id도 받지 않음
- 로그인 회원 id로 예약 생성
- 내 예약 조회도 로그인 회원 id 기준
- 예약 수정/취소도 로그인 회원의 예약인지 확인

#### 인증 실패 응답 방식

- 인증 정보 없음: `401 Unauthorized`
- 세션 만료 또는 유효하지 않은 세션: `401 Unauthorized`
- 로그인 실패: `401 Unauthorized`
- 응답 형식 동일

```json
{
  "message": "로그인이 필요합니다."
}
```

### Todo

- [x] 회원 도메인과 저장소를 추가한다.
- [x] 회원 컨트롤러를 추가한다.
- [x] 회원가입 요청/응답 DTO를 만든다.
- [x] 이메일 중복을 검증하는 회원가입 기능을 구현한다.
- [x] 로그인 요청/응답 DTO를 만든다.
- [x] 비밀번호 검증 후 세션에 로그인 회원 식별자를 저장하는 로그인 기능을 구현한다.
- [x] 세션을 무효화하는 로그아웃 기능을 구현한다.
- [x] `@LoginMember` 어노테이션을 만든다.
- [x] 로그인 회원 정보를 컨트롤러 파라미터로 주입하는 `LoginMemberArgumentResolver`를 구현한다.
- [x] 예약 생성 시 요청의 이름 대신 로그인 회원 id를 사용하도록 변경한다.
- [x] 예약 조회 시 이름 쿼리 파라미터 대신 로그인 회원 id를 사용하도록 변경한다.
- [x] 예약 취소/수정 시 로그인 회원 id를 사용하도록 변경한다.
- [x] 인증 실패 응답을 공통 예외 처리로 정리한다.
- [x] 회원가입 성공/실패 테스트를 작성한다.
- [x] 로그인 성공/실패 테스트를 작성한다.
- [x] 로그아웃 응답 테스트를 작성한다.
- [x] 로그아웃 후 인증 API 접근 실패 테스트를 작성한다.
- [x] 비로그인 사용자의 예약 생성 실패 테스트를 작성한다.
- [x] 로그인 사용자의 예약 생성 테스트를 작성한다.
- [x] 로그인 사용자의 내 예약 조회 테스트를 작성한다.

---

## 2단계: 모바일 앱 요청 인증하기

### 요구 사항

#### 모바일 로그인

- [x] 모바일 앱 사용자는 로그인할 수 있다.
- [x] 로그인 성공 후 모바일 앱이 이후 요청에 사용할 인증 정보를 받을 수 있다.
- [x] 인증 정보는 이후 요청마다 서버가 사용자를 식별할 수 있는 형태여야 한다.

#### 모바일 인증 요청

- [x] 모바일 앱은 인증이 필요한 API를 호출할 때 인증 정보를 함께 전달한다.
- [x] 서버는 전달된 인증 정보를 검증한다.
- [x] 인증 정보가 유효하면 로그인한 사용자로 요청을 처리한다.
- [x] 인증 정보가 없거나 유효하지 않으면 요청을 거부한다.

#### 웹 인증과의 관계

- [x] 웹 인증 흐름과 모바일 인증 흐름의 공통점을 설명할 수 있다.
- [x] 웹 인증 흐름과 모바일 인증 흐름의 차이점을 설명할 수 있다.
- [x] 가능한 한 중복된 인증 로직을 줄인다.

### API 명세

2단계에서 새로 추가하거나 변경한 API만 작성한다.

| 구분 | 기능 | Method | URL | 인증 | 설명 | Request Body | 성공 응답 | 실패 응답 |
|----|----|----|----|----|----|----|----|----|
| 추가 | 모바일 로그인 | `POST` | `/mobile/login` | 불필요 | 모바일 요청에 사용할 토큰 발급 | `email`, `password` | `200 OK`, 토큰 정보 | `401 Unauthorized` |
| 변경 | 내 정보 조회 | `GET` | `/members/me` | 필요 | 세션 또는 토큰으로 로그인 회원 조회 | 없음 | `200 OK`, 회원 정보 | `401 Unauthorized` |
| 변경 | 예약 생성 | `POST` | `/reservations` | 필요 | 세션 또는 토큰으로 로그인 회원 식별 | `date`, `timeId`, `themeId` | `201 Created`, 예약 정보 | `401 Unauthorized` |
| 변경 | 내 예약 조회 | `GET` | `/reservations` | 필요 | 세션 또는 토큰으로 로그인 회원의 예약 조회 | 없음 | `200 OK`, 예약 목록 | `401 Unauthorized` |

### 요청/응답 예시

| 기능 | 예시 |
|----|----|
| 모바일 로그인 요청 | `{ "email": "member@example.com", "password": "password" }` |
| 모바일 로그인 응답 | `{ "accessToken": "uuid-token", "tokenType": "Bearer", "expiresIn": 3600 }` |
| 모바일 인증 헤더 | `Authorization: Bearer {accessToken}` |
| 모바일 내 정보 조회 | `GET /members/me` + `Authorization` 헤더 |
| 모바일 예약 생성 | `POST /reservations` + `Authorization` 헤더 |
| 인증 실패 응답 | `{ "message": "로그인이 필요합니다." }` |

### 상세 설명

#### 모바일 앱 요청의 인증 방식

- 모바일은 토큰 방식 사용
- 로그인 성공 시 UUID 기반 `accessToken` 발급
- 서버는 `accessToken -> memberId, expiresAt` 정보를 저장
- 토큰 만료 시간: 1시간
- 만료된 토큰은 인증 실패 처리

#### UUID 토큰을 선택한 이유

- 토큰은 예측하기 어려운 랜덤 문자열이어야 함
- `UUID.randomUUID()`는 학습용 랜덤 토큰으로 단순하고 충분함
- `email/password` 인코딩은 사용하지 않음
- Base64 인코딩은 암호화가 아니므로 토큰 유출 시 계정 정보도 함께 노출될 수 있음

#### 인증 정보 전달 위치

- `Authorization` 헤더 사용
- 형식: `Authorization: Bearer {accessToken}`
- 쿠키는 웹 세션 인증에 사용
- 커스텀 헤더는 표준성이 낮아 선택하지 않음

#### Bearer를 선택한 이유

- `Bearer`는 토큰 소유자를 인증된 사용자로 보는 토큰 전달 방식
- 로그인 후 발급받은 토큰을 보내는 구조에 적합
- `Basic`은 매 요청마다 `email:password`를 보내는 방식이라 이번 구조와 맞지 않음

#### 웹 인증 흐름과 모바일 인증 흐름의 통합 방식

- 컨트롤러는 웹/모바일을 구분하지 않음
- 인증이 필요한 컨트롤러는 그대로 `@LoginMember Member member` 사용
- `LoginMemberArgumentResolver`가 세션과 토큰을 모두 확인
- 세션에 `loginMemberId`가 있으면 웹 사용자로 식별
- 세션이 없으면 `Authorization` 헤더의 Bearer 토큰으로 모바일 사용자 식별

#### 웹 인증과 모바일 인증 비교

| 구분 | 웹 | 모바일 |
|----|----|----|
| 인증 방식 | 세션 | 토큰 |
| 인증 정보 전달 위치 | 쿠키 `JSESSIONID` | `Authorization` 헤더 |
| 인증 정보 형식 | 세션 id | `Bearer {accessToken}` |
| 로그인 성공 응답 | 회원 정보 + 세션 쿠키 | 토큰 정보 |
| 만료/로그아웃 | 세션 무효화 | 토큰 만료 시간 |

공통점:

- 이메일과 비밀번호로 로그인
- 인증 정보에서 회원 id를 찾음
- `@LoginMember Member`로 로그인 사용자 전달
- 인증 실패 시 `401 Unauthorized`
- 응답 형식 동일

차이점:

- 웹은 브라우저가 쿠키를 자동 전송
- 모바일은 앱이 토큰을 저장하고 `Authorization` 헤더에 직접 추가
- 웹 로그아웃은 세션 무효화
- 모바일은 현재 토큰 만료 시간에 맡김

#### 인증 실패 응답 방식

- 웹과 모바일 모두 동일한 응답 사용
- 인증 정보가 없거나 유효하지 않으면 `401 Unauthorized`
- 클라이언트별 응답을 나누지 않음

```json
{
  "message": "로그인이 필요합니다."
}
```

### Postman 테스트 방법

#### 1. 모바일 로그인

`POST http://localhost:8080/mobile/login`

Headers:

```text
Content-Type: application/json
```

Body:

```json
{
  "email": "milan@example.com",
  "password": "password"
}
```

응답:

```json
{
  "accessToken": "uuid-token",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

#### 2. 토큰으로 내 정보 조회

`GET http://localhost:8080/members/me`

Headers:

```text
Authorization: Bearer {accessToken}
```

#### 3. 토큰으로 예약 생성

`POST http://localhost:8080/reservations`

Headers:

```text
Content-Type: application/json
Authorization: Bearer {accessToken}
```

Body:

```json
{
  "date": "2099-05-20",
  "timeId": 1,
  "themeId": 1
}
```

#### 4. 실패 확인

토큰 없이 인증 API 호출:

```text
GET http://localhost:8080/members/me
```

잘못된 토큰으로 호출:

```text
Authorization: Bearer wrong-token
```

응답:

```json
{
  "message": "로그인이 필요합니다."
}
```

### Todo

- [x] 모바일 로그인 API를 추가한다.
- [x] UUID 기반 토큰을 발급한다.
- [x] 토큰 만료 시간을 둔다.
- [x] `Authorization: Bearer {accessToken}` 헤더에서 토큰을 읽는다.
- [x] `LoginMemberArgumentResolver`에서 세션과 토큰을 함께 지원한다.
- [x] 모바일 로그인 성공/실패 테스트를 작성한다.
- [x] 모바일 토큰으로 인증 API 접근 테스트를 작성한다.
- [x] 유효하지 않은 모바일 토큰 인증 실패 테스트를 작성한다.

---

## 3단계: 인가 - 자기 매장 예약만 관리하기

### 요구 사항

#### 매장 매니저 식별

- [ ] 로그인한 사용자가 매장 매니저인지 확인할 수 있어야 한다.
- [ ] 매장 매니저가 어떤 매장을 관리하는지 확인할 수 있어야 한다.

#### 예약 접근 제한

- [ ] 매장 매니저는 자기 매장의 예약만 조회할 수 있다.
- [ ] 매장 매니저는 자기 매장의 예약만 변경할 수 있다.
- [ ] 매장 매니저는 자기 매장의 예약만 삭제할 수 있다.
- [ ] 다른 매장의 예약에 접근하려는 요청은 거부한다.

#### 실패 처리

- [ ] 로그인하지 않은 요청은 인증 실패로 처리한다.
- [ ] 로그인했지만 권한이 없는 요청은 인가 실패로 처리한다.
- [ ] 인증 실패와 인가 실패를 같은 문제로 처리하지 않는다.

### API 명세

3단계에서 새로 추가하거나 변경할 API만 작성한다.

| 구분 | 기능 | Method | URL | 인증 | 설명 | Request Body | 성공 응답 | 실패 응답 |
|----|----|----|----|----|----|----|----|----|
| 변경 | 관리자 예약 조회 | `GET` | `/admin/reservations` | 필요 | 로그인한 매니저가 관리하는 매장의 예약 목록만 조회 | 없음 | `200 OK`, 예약 목록 | `401 Unauthorized`, `403 Forbidden` |
| 추가 | 관리자 예약 변경 | `PATCH` | `/admin/reservations/{id}` | 필요 | 자기 매장 예약만 변경 | `date`, `timeId`, `themeId` | `200 OK`, 예약 정보 | `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found` |
| 변경 | 관리자 예약 삭제 | `DELETE` | `/admin/reservations/{id}` | 필요 | 자기 매장 예약만 삭제 | 없음 | `204 No Content` | `401 Unauthorized`, `403 Forbidden`, `404 Not Found` |
| 변경 | 예약 응답 | - | - | - | 예약이 속한 매장을 확인할 수 있도록 `storeId` 포함 | - | 예약 정보에 `storeId` 포함 | - |

### 요청/응답 예시

| 기능 | 예시 |
|----|----|
| 관리자 예약 조회 요청 | `GET /admin/reservations` |
| 관리자 예약 변경 요청 | `PATCH /admin/reservations/1` |
| 관리자 예약 변경 Body | `{ "date": "2026-05-21", "timeId": 1, "themeId": 1 }` |
| 관리자 예약 삭제 요청 | `DELETE /admin/reservations/1` |
| 예약 응답 | `{ "id": 1, "memberId": 1, "storeId": 1, "date": "2026-05-21", "time": { ... }, "theme": { ... } }` |
| 인증 실패 응답 | `{ "message": "로그인이 필요합니다." }` |
| 인가 실패 응답 | `{ "message": "접근 권한이 없습니다." }` |

### 상세 설명

#### 인가 판단 위치

- 인증은 기존 `@LoginMember`와 `LoginMemberArgumentResolver`를 사용한다.
- 인가는 기존 관리자 예약 기능을 처리하는 서비스 계층에서 판단한다.
- 컨트롤러는 로그인한 회원 정보를 전달하고, 권한 비교 로직은 직접 가지지 않는다.
- 서비스는 로그인 회원의 매장과 접근하려는 예약의 매장을 비교한다.

#### 매장 매니저와 매장의 관계 표현 방식

- 회원은 `role`을 가진다.
- 회원 역할은 `USER`, `MANAGER`로 구분한다.
- 매장 정보를 표현하는 `Store` 도메인을 추가한다.
- 회원은 자신이 관리하는 매장 id인 `storeId`를 가진다.
- 일반 사용자의 `storeId`는 비워둘 수 있다.
- 3단계에서는 한 명의 매니저가 하나의 매장만 관리한다고 가정한다.

#### 예약 접근 권한 확인 방식

- 예약은 자신이 속한 매장 id를 가진다.
- 관리자 예약 조회는 로그인한 매니저의 매장 id를 기준으로 조회한다.
- 예약 변경/삭제는 예약을 조회한 뒤 예약의 매장 id와 매니저의 매장 id를 비교한다.
- 두 매장 id가 다르면 인가 실패로 처리한다.

#### 인증 실패와 인가 실패 구분

- 로그인하지 않은 요청: `401 Unauthorized`
- 토큰 또는 세션이 유효하지 않은 요청: `401 Unauthorized`
- 로그인했지만 매니저가 아닌 요청: `403 Forbidden`
- 로그인한 매니저가 다른 매장 예약에 접근한 요청: `403 Forbidden`

```json
{
  "message": "접근 권한이 없습니다."
}
```

### Todo

- [x] 매장과 매니저 정보를 표현한다.
  - [x] `Store` 도메인과 저장소를 추가한다.
  - [x] `Member`에 `role`, `storeId`를 추가한다.
  - [x] 회원 역할은 `USER`, `MANAGER`로 구분한다.

- [x] 예약과 매장을 연결한다.
  - [x] 예약에 `storeId`를 추가한다.
  - [x] 예약 생성, 조회, 응답에서 매장 정보를 다룰 수 있게 변경한다.

- [ ] 기존 관리자 예약 API에 인가를 적용한다.
  - `GET /admin/reservations`는 매니저의 자기 매장 예약만 조회한다.
  - `PATCH /admin/reservations/{id}`를 추가하고 자기 매장 예약만 변경한다.
  - `DELETE /admin/reservations/{id}`는 자기 매장 예약만 삭제한다.

- [ ] 인증 실패와 인가 실패를 구분한다.
  - 비로그인 요청은 `401 Unauthorized`로 처리한다.
  - 매니저가 아닌 사용자나 다른 매장 예약 접근은 `403 Forbidden`으로 처리한다.
  - 인가 판단 로직은 컨트롤러에 흩어지지 않도록 서비스 계층에 둔다.

- [ ] 인가 테스트를 작성한다.
  - 비로그인 사용자의 관리자 예약 API 접근 실패를 검증한다.
  - 매니저가 아닌 사용자의 관리자 예약 API 접근 실패를 검증한다.
  - 매니저가 자기 매장 예약을 조회, 변경, 삭제할 수 있는지 검증한다.
  - 매니저가 다른 매장 예약을 조회, 변경, 삭제할 수 없는지 검증한다.
