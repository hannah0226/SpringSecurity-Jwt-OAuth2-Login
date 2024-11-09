# SpringSecurityJwtOAuth2
Spring Security + Jwt 자체 로그인 &amp; OAuth2 로그인 공부 레포지토리

## 주요 기능
1. **일반 로그인**: 이메일과 비밀번호를 이용한 로그인이다. 비밀번호는 BCrypt 해시 방식을 사용해 암호화된다.
2. **OAuth2 로그인**: 구글과 카카오를 이용한 로그인이다. OAuth2 로그인을 통해 사용자를 인증하며, 해당 소셜 계정의 사용자 정보를 받아와 저장한다.
3. **추가 정보 저장**: OAuth2 로그인 후 추가 정보를 등록한다. OAuth2 로그인 시 초기 권한은 GUEST로 설정되고, 추가 정보를 등록하면 USER 권한으로 변경된다. (일반 로그인은 회원가입 시 한번에 등록 가능하므로, 바로 USER권한을 가짐)
4. **JWT를 사용한 사용자 인증**: JWT를 사용해 AccessToken과 RefreshToken을 발급하고, 이를 사용해 API 요청 시 사용자를 인증한다.
5. **Redis를 사용한 리프레시 토큰 관리**: JWT의 유효기간이 만료되었을 때, Redis에 저장된 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급한다.

## 사용 기술 스택
| 기술 스택               | 설명         |
|-----------------------|------------|
| **Spring Boot**       | 버전 3.3.4   |
| **Java**              | 버전 17      |
| **Gradle**            | 프로젝트 빌드 관리 도구 |
| **Spring Security**   | 인증 및 인가 관리 |
| **JWT**               | 토큰 기반 인증   |
| **OAuth2**            | 소셜 로그인     |
| **MySQL**             | 사용자 정보 저장  |
| **Redis**             | 리프레시 토큰 저장 |
| **BCrypt**            | 비밀번호 암호화   |

## API 설명

### 1. `/auth/signup` - 회원가입
- 사용자가 회원가입을 요청하면 이메일과 닉네임의 중복을 검사하고, 비밀번호는 BCrypt로 암호화하여 데이터베이스에 저장한다.
- **Request Body**
  ```json
  {
    "email": "user@example.com",
    "password": "userpassword",
    "nickname": "user",
    "age": "20",
    "city": "SEOUL"
  }  
  ```
- **Success Response**
  ```json
  {
    "message": "회원가입이 완료되었습니다."
  }
  ```

### 2. `/auth/login` - 일반 로그인
- 사용자가 로그인을 요청하면 이메일로 사용자를 찾고 비밀번호를 검증한다. 성공적으로 인증되면 AccessToken과 RefreshToken을 생성하여 반환하고, RefreshToken은 Redis에 저장한다.
- **Request Body**
  ```json
  {
    "email": "user@example.com",
    "password": "userpassword"
  }  
  ```
- **Success Response**
  ```json
  {
    "accessToken": "ACCESS_TOKEN",
    "refreshToken": "REFRESH_TOKEN"
  }
  ```

### 3. `/oauth2/authorization/google` `/oauth2/authorization/kakao` - OAuth2 구글/카카오 로그인
- 구글/카카오 OAuth2 로그인 페이지로 리디렉션하여 사용자가 구글/카카오 계정을 통해 로그인할 수 있도록 한다. 로그인 후, 구글/카카오에서 제공하는 사용자 정보를 통해 회원 정보를 등록(or 갱신)하고, 인증 토큰을 발급한다.
- **동작 방식(구글)**
    1. 사용자가 /oauth2/authorization/google로 접근하면, Spring Security의 OAuth2 설정에 따라 구글 인증 페이지로 리디렉션된다.
    2. 사용자가 구글 계정을 통해 인증을 완료하면, 구글은 사용자 정보와 함께 애플리케이션으로 다시 리디렉션한다.
    3. Spring Security의 CustomOAuth2UserService가 이 사용자 정보를 받아 처리한다.:
        - `OAuth2UserInfo`: 구글에서 받은 사용자 정보를 표준화하여 처리할 수 있도록 GoogleOAuth2UserInfo 클래스에서 ID, 이메일, 닉네임 등의 데이터를 추출한다.
        - `User 조회`: 이메일을 기반으로 데이터베이스에서 사용자를 조회한다.
        - `신규 사용자 등록`: 기존에 가입된 사용자가 없을 경우, 새로운 User 엔티티를 생성하고 DB에 저장한다. 초기 권한은 GUEST로 설정된다.
    4. 인증 토큰 생성:
        - OAuth2AuthenticationSuccessHandler는 인증이 완료된 사용자에 대해 TokenProvider를 통해 AccessToken과 RefreshToken을 생성하고, RefreshToken은 Redis에 저장한다.
        - 생성된 토큰은 JSON 응답으로 반환되며, 클라이언트는 이를 저장하고 이후 요청 시 인증 수단으로 사용한다.

### 4. `/auth/update-info` - OAuth2 로그인 시 추가 정보 등록
- OAuth2 로그인을 완료한 후, 추가로 필요한 정보(나이, 도시)를 사용자가 입력할 수 있도록 한다. 추가 정보를 등록하면 사용자의 권한을 GUEST에서 USER로 변경하고 업데이트된 정보를 저장한다.
- **Request Body**
  ```json
  {
    "age": "20",
    "city": "SEOUL"
  }
  ```
- **Success Response**
  ```json
  {
    "message": "사용자 정보가 저장되었습니다."
  }
  ```

### 5. `/auth/token` - 액세스 토큰 재발급
- 만료된 액세스 토큰 대신 새로운 액세스 토큰을 발급한다. 전달된 리프레시 토큰의 유효성을 검사하고, Redis에 저장된 리프레시 토큰과 일치하는 경우 새로운 액세스 토큰을 반환한다.
- **Request Body**
  ```json
  {
    "refreshToken": "REFRESH_TOKEN"
  }
  ```
- **Success Response**
  ```json
  {
    "accessToken": "NEW_ACCESS_TOKEN"
  }
  ```

### 6. `/auth/users/me` - 사용자 프로필 조회
- 현재 인증된 사용자의 이메일을 반환한다. JWT 인증을 통해 현재 로그인된 사용자를 식별하고, 해당 사용자의 이메일을 응답으로 보낸다.
- **Success Response**
  ```json
  {
    "email": "user@example.com"
  }
  ```

