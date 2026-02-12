# Eum-SSO REST API 명세서

## 1. 공통 사항

### 1.1. Base URL

- `http://localhost:8080`

### 1.2. 인증 (Authentication)

- 본 명세서에 기술된 대부분의 API는 인증이 필요합니다.
- OIDC 인증을 통해 발급받은 **Access Token**을 HTTP 헤더에 담아 요청해야 합니다.
  ```
  Authorization: Bearer <ACCESS_TOKEN>
  ```

### 1.3. 권한 (Authorization)

- 대부분의 관리용 API는 `ROLE_ADMIN` 권한을 가진 사용자만 호출할 수 있습니다.
- 각 API에 필요한 권한은 `SecurityConfig.kt` 파일에 정의되어 있습니다.

### 1.4. 공통 에러 응답 형식

- **400 Bad Request**: 요청이 잘못되었을 경우 (예: 필수 필드 누락, 유효성 검사 실패)
  ```json
  {
    "error": "상세 에러 메시지"
  }
  ```
- **404 Not Found**: 요청한 리소스를 찾을 수 없을 경우
  ```json
  {
    "error": "User not found with user_id: a"
  }
  ```
- **401 Unauthorized**: 인증되지 않았거나 토큰이 유효하지 않은 경우 (응답 본문 없음)
- **403 Forbidden**: 인증은 되었으나 해당 리소스에 접근할 권한이 없는 경우 (응답 본문 없음)

---

## 2. User API (`/api/v1/users`)

사용자 계정 및 역할 관리를 위한 API입니다.

- **`POST /api/v1/users/register`**: 신규 사용자 등록
  - **Request Body**: `UserRegistrationRequest`
    ```json
    {
      "userId": "testuser",
      "username": "테스트 사용자",
      "email": "test@example.com",
      "password": "password123",
      "orgId": "a1b2c3d4-...",
      "deptName": "개발팀",
      "phoneNumber": "010-1234-5678",
      "userRole": "STAFF",
      "roles": ["ROLE_USER"]
    }
    ```
  - **Success Response**: `201 Created`
    ```json
    {
      "message": "User registered successfully",
      "userId": "testuser"
    }
    ```

- **`GET /api/v1/users`**: 모든 사용자 목록 조회
  - **Success Response**: `200 OK`, `List<UserResponse>`

- **`GET /api/v1/users/{userId}`**: 특정 사용자 정보 조회
  - **Path Variable**: `userId` (String)
  - **Success Response**: `200 OK`, `UserResponse`

- **`PUT /api/v1/users/{userId}`**: 사용자 정보 수정
  - **Path Variable**: `userId` (String)
  - **Request Body**: `UserUpdateRequest`
    ```json
    {
      "username": "수정된 이름",
      "phoneNumber": "010-9999-8888"
    }
    ```
  - **Success Response**: `200 OK`, `UserResponse`

- **`DELETE /api/v1/users/{userId}`**: 사용자 비활성화 (소프트 삭제)
  - **Path Variable**: `userId` (String)
  - **Success Response**: `204 No Content`

- **`POST /api/v1/users/{userId}/roles`**: 사용자에게 역할 추가
  - **Path Variable**: `userId` (String)
  - **Request Body**:
    ```json
    {
      "roleName": "ROLE_ADMIN"
    }
    ```
  - **Success Response**: `200 OK`, `UserResponse`

- **`DELETE /api/v1/users/{userId}/roles`**: 사용자의 역할 제거
  - **Path Variable**: `userId` (String)
  - **Request Body**:
    ```json
    {
      "roleName": "ROLE_ADMIN"
    }
    ```
  - **Success Response**: `200 OK`, `UserResponse`

---

## 3. Role API (`/api/v1/roles`)

시스템의 역할(권한)을 관리하는 API입니다.

- **`POST /api/v1/roles`**: 새로운 역할 생성
  - **Request Body**: `RoleCreateRequest`
    ```json
    {
      "name": "ROLE_NEW_FEATURE"
    }
    ```
  - **Success Response**: `201 Created`, `RoleResponse`

- **`GET /api/v1/roles`**: 모든 역할 목록 조회
  - **Success Response**: `200 OK`, `List<RoleResponse>`

- **`GET /api/v1/roles/{id}`**: 특정 역할 조회
  - **Path Variable**: `id` (Long)
  - **Success Response**: `200 OK`, `RoleResponse`

- **`DELETE /api/v1/roles/{id}`**: 역할 삭제
  - **Path Variable**: `id` (Long)
  - **Success Response**: `204 No Content`

---

## 4. Organization API (`/api/v1/orgs`)

조직(회사) 정보를 관리하는 API입니다.

- **`POST /api/v1/orgs`**: 신규 조직 생성
  - **Request Body**: `OrgCreateRequest`
  - **Success Response**: `201 Created`, `OrgResponse`

- **`GET /api/v1/orgs`**: 모든 조직 목록 조회
  - **Success Response**: `200 OK`, `List<OrgResponse>`

- **`GET /api/v1/orgs/{id}`**: 특정 조직 정보 조회
  - **Path Variable**: `id` (UUID)
  - **Success Response**: `200 OK`, `OrgResponse`

- **`PUT /api/v1/orgs/{id}`**: 조직 정보 수정
  - **Path Variable**: `id` (UUID)
  - **Request Body**: `OrgUpdateRequest`
  - **Success Response**: `200 OK`, `OrgResponse`

- **`DELETE /api/v1/orgs/{id}`**: 조직 비활성화 (소프트 삭제)
  - **Path Variable**: `id` (UUID)
  - **Success Response**: `204 No Content`

---

## 5. Board API (`/api/v1/boards`)

게시판(공지사항 등)을 관리하는 API입니다.

- **`POST /api/v1/boards`**: 새 게시글 작성
  - **Request Body**: `BoardCreateRequest`
  - **Success Response**: `201 Created`, `BoardResponse`

- **`GET /api/v1/boards`**: 모든 게시글 목록 조회
  - **Success Response**: `200 OK`, `List<BoardResponse>`

- **`GET /api/v1/boards/{id}`**: 특정 게시글 조회
  - **Path Variable**: `id` (UUID)
  - **Success Response**: `200 OK`, `BoardResponse`

- **`PUT /api/v1/boards/{id}`**: 게시글 수정
  - **Path Variable**: `id` (UUID)
  - **Request Body**: `BoardUpdateRequest`
  - **Success Response**: `200 OK`, `BoardResponse`

- **`DELETE /api/v1/boards/{id}`**: 게시글 삭제 (소프트 삭제)
  - **Path Variable**: `id` (UUID)
  - **Success Response**: `204 No Content`

---

## 6. 기타 관리 API

### 6.1. S3 File API (`/api/v1/s3files`)

- **`POST /`**: S3 파일 정보 생성
- **`GET /`**: 모든 S3 파일 정보 조회
- **`GET /{id}`**: 특정 S3 파일 정보 조회
- **`PUT /{id}`**: S3 파일 정보 수정
- **`DELETE /{id}`**: S3 파일 정보 삭제 (소프트 삭제)

### 6.2. CSO Partner API (`/api/v1/cso-partners`)

- **`POST /`**: CSO 파트너 정보 생성
- **`GET /`**: 모든 CSO 파트너 정보 조회
- **`GET /{id}`**: 특정 CSO 파트너 정보 조회
- **`PUT /{id}`**: CSO 파트너 정보 수정
- **`DELETE /{id}`**: CSO 파트너 정보 삭제 (소프트 삭제)

### 6.3. Client Details Info API (`/api/v1/client-details`)

- **`POST /`**: 클라이언트 부가 정보 생성
- **`GET /`**: 모든 클라이언트 부가 정보 조회
- **`GET /{clientId}`**: 특정 클라이언트 부가 정보 조회
- **`PUT /{clientId}`**: 클라이언트 부가 정보 수정
- **`DELETE /{clientId}`**: 클라이언트 부가 정보 삭제
