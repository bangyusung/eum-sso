-- 1. 확장 기능 활성화 (UUID 생성용)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. 공통 유틸리티: 수정 시간 자동 업데이트 함수
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- 3. 사용자 및 권한 관리 (RBAC) 테이블
-- username, email의 UNIQUE 제약조건은 제거하고, 수기 입력 식별자인 user_id를 PK처럼 활용
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(), -- 시스템 내부 관리용 PK
    user_id VARCHAR(100) NOT NULL UNIQUE,          -- 사용자가 수기 입력하는 고유 ID (SSO 인증 주체)
    username VARCHAR(50) NOT NULL,                 -- 중복 허용
    password VARCHAR(255) NOT NULL,                -- 암호화된 비밀번호
    email VARCHAR(100) NOT NULL,                   -- 중복 허용
    enabled BOOLEAN DEFAULT TRUE NOT NULL,
    account_non_locked BOOLEAN DEFAULT TRUE NOT NULL,
    account_non_expired BOOLEAN DEFAULT TRUE NOT NULL,
    credentials_non_expired BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
    );

COMMENT ON TABLE users IS '서비스 사용자 정보 (user_id를 통해 인증 시스템과 연동)';
COMMENT ON COLUMN users.id IS '시스템 내부 관리용 UUID';
COMMENT ON COLUMN users.user_id IS '비즈니스 식별자 및 로그인 시 principal로 사용되는 고유 ID';

CREATE TABLE IF NOT EXISTS roles (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE
    );

COMMENT ON TABLE roles IS '사용자 권한(역할) 정의 테이블';

CREATE TABLE IF NOT EXISTS users_roles (
                                           user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
    );

COMMENT ON TABLE users_roles IS '사용자(UUID 기반)-권한 매핑 테이블';

-- 4. 애플리케이션(클라이언트) 관리 테이블
CREATE TABLE IF NOT EXISTS oauth2_registered_client (
                                                        id varchar(100) NOT NULL PRIMARY KEY,
    client_id varchar(100) NOT NULL UNIQUE,
    client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret varchar(200) DEFAULT NULL,
    client_secret_expires_at timestamp DEFAULT NULL,
    client_name varchar(200) NOT NULL,
    client_authentication_methods varchar(1000) NOT NULL,
    authorization_grant_types varchar(1000) NOT NULL,
    redirect_uris varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris varchar(1000) DEFAULT NULL,
    scopes varchar(1000) NOT NULL,
    client_settings varchar(2000) NOT NULL,
    token_settings varchar(2000) NOT NULL
    );

COMMENT ON TABLE oauth2_registered_client IS '인증을 허용할 클라이언트(Application) 목록';

CREATE TABLE IF NOT EXISTS client_details_info (
                                                   client_id VARCHAR(100) PRIMARY KEY,
    app_name VARCHAR(100) NOT NULL,
    description TEXT,
    owner_email VARCHAR(100),
    app_status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_client_info FOREIGN KEY (client_id)
    REFERENCES oauth2_registered_client(client_id) ON DELETE CASCADE
    );

-- 5. 인증 내역 및 세션 정보 테이블 (Spring 엔진용)
-- principal_name 컬럼에 users.user_id가 저장되어 논리적 관계를 맺음
CREATE TABLE IF NOT EXISTS oauth2_authorization (
                                                    id varchar(100) NOT NULL PRIMARY KEY,
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL, -- users.user_id 와 매핑되는 지점
    authorization_grant_type varchar(100) NOT NULL,
    authorized_scopes varchar(1000) DEFAULT NULL,
    attributes varchar(4000) DEFAULT NULL,
    state varchar(500) DEFAULT NULL,
    authorization_code_value bytea DEFAULT NULL,
    authorization_code_issued_at timestamp DEFAULT NULL,
    authorization_code_expires_at timestamp DEFAULT NULL,
    authorization_code_metadata varchar(2000) DEFAULT NULL,
    access_token_value bytea DEFAULT NULL,
    access_token_issued_at timestamp DEFAULT NULL,
    access_token_expires_at timestamp DEFAULT NULL,
    access_token_metadata varchar(2000) DEFAULT NULL,
    access_token_type varchar(100) DEFAULT NULL,
    access_token_scopes varchar(1000) DEFAULT NULL,
    oidc_id_token_value bytea DEFAULT NULL,
    oidc_id_token_issued_at timestamp DEFAULT NULL,
    oidc_id_token_expires_at timestamp DEFAULT NULL,
    oidc_id_token_metadata varchar(2000) DEFAULT NULL,
    refresh_token_value bytea DEFAULT NULL,
    refresh_token_issued_at timestamp DEFAULT NULL,
    refresh_token_expires_at timestamp DEFAULT NULL,
    refresh_token_metadata varchar(2000) DEFAULT NULL,
    user_code_value bytea DEFAULT NULL,
    user_code_issued_at timestamp DEFAULT NULL,
    user_code_expires_at timestamp DEFAULT NULL,
    user_code_metadata varchar(2000) DEFAULT NULL,
    device_code_value bytea DEFAULT NULL,
    device_code_issued_at timestamp DEFAULT NULL,
    device_code_expires_at timestamp DEFAULT NULL,
    device_code_metadata varchar(2000) DEFAULT NULL,
    CONSTRAINT fk_auth_client FOREIGN KEY (registered_client_id)
    REFERENCES oauth2_registered_client(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS oauth2_authorization_consent (
                                                            registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL, -- users.user_id 와 매핑되는 지점
    authorities varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name),
    CONSTRAINT fk_consent_client FOREIGN KEY (registered_client_id)
    REFERENCES oauth2_registered_client(id) ON DELETE CASCADE
    );

-- 6. 트리거 적용
CREATE TRIGGER update_users_modtime BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_client_details_modtime BEFORE UPDATE ON client_details_info FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 7. 초기 데이터 (역할)
INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN') ON CONFLICT DO NOTHING;