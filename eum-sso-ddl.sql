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
create table users
(
    id                      uuid                     default uuid_generate_v4()         not null
        primary key,
    user_id                 varchar(100)                                                not null
        unique,
    username                varchar(50)                                                 not null,
    password                varchar(255)                                                not null,
    email                   varchar(100)                                                not null,
    enabled                 boolean                  default true                       not null,
    account_non_locked      boolean                  default true                       not null,
    account_non_expired     boolean                  default true                       not null,
    credentials_non_expired boolean                  default true                       not null,
    created_at              timestamp with time zone default CURRENT_TIMESTAMP          not null,
    updated_at              timestamp with time zone default CURRENT_TIMESTAMP          not null,
    org_id                  uuid
        constraint fk_users_org_id
            references orgs
            on delete set null,
    dept_name               varchar(100),
    phone_number            varchar(20),
    user_role               varchar(20)              default 'STAFF'::character varying not null,
    deleted                 boolean                  default false                      not null,
    last_login_at           timestamp with time zone
);

comment on table users is '서비스 사용자 정보 (user_id를 통해 인증 시스템과 연동)';

comment on column users.id is '시스템 내부 관리용 UUID';

comment on column users.user_id is '비즈니스 식별자 및 로그인 시 principal로 사용되는 고유 ID';

comment on column users.org_id is '소속 조직 ID';

comment on column users.user_role is '권한(ADMIN, STAFF)';


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


-- 조직(제약사, 위탁사) 테이블
CREATE TABLE orgs (
                      id          uuid                     DEFAULT uuid_generate_v4() PRIMARY KEY,
                      org_type    varchar(20)              NOT NULL, -- PHARMA, CSO
                      biz_name    varchar(100)             NOT NULL,
                      biz_number  varchar(20)              NOT NULL UNIQUE,
                      rep_name    varchar(50),
                      address     varchar(255),
                      biz_doc_url varchar(500),
                      status      varchar(20)              DEFAULT 'NORMAL' NOT NULL, -- NORMAL, CLOSED, SUSPENDED
                      deleted     boolean                  DEFAULT false NOT NULL,
                      modified_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                      created_at  timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);

-- 파일 관리 테이블
CREATE TABLE s3files (
                         id          uuid                     DEFAULT uuid_generate_v4() PRIMARY KEY,
                         url         varchar(500)             NOT NULL,
                         file_size   bigint,
                         orphaned    boolean                  DEFAULT false,
                         deleted     boolean                  DEFAULT false,
                         uploaded_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                         modified_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                         created_at  timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);

-- 주석 추가 (PostgreSQL 스타일)
COMMENT ON COLUMN users.org_id IS '소속 조직 ID';
COMMENT ON COLUMN users.user_role IS '권한(ADMIN, STAFF)';


-- 거래처 마스터 (임시/정식 통합)
CREATE TABLE cso_partners (
                              id          uuid                     DEFAULT uuid_generate_v4() PRIMARY KEY,
                              org_id      uuid                     NULL,
                              biz_name    varchar(100)             NOT NULL,
                              biz_number  varchar(20)              NOT NULL UNIQUE,
                              rep_name    varchar(50),
                              address     varchar(255),
                              deleted     boolean                  DEFAULT false,
                              modified_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                              created_at  timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT fk_partners_org_id FOREIGN KEY (org_id) REFERENCES orgs(id)
);

-- 재위탁 보고서 메인
CREATE TABLE cso_re_entrustment_reports (
                                            id               uuid                     DEFAULT uuid_generate_v4() PRIMARY KEY,
                                            pharma_org_id    uuid                     NOT NULL,
                                            cso_org_id       uuid                     NOT NULL,
                                            target_partner_id uuid                    NOT NULL,
                                            approval_status  varchar(20)              DEFAULT 'READY', -- READY, APPROVED, REJECTED
                                            delivery_status  varchar(20)              DEFAULT 'PROGRESS', -- DELIVERED, PROGRESS, FAILED
                                            contract_start_date date,
                                            contract_end_date   date,
                                            notice_date         date,
                                            report_date         timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                                            report_user_id      uuid                    NOT NULL,
                                            deleted             boolean                  DEFAULT false,
                                            modified_at         timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                                            created_at          timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                                            CONSTRAINT fk_report_pharma FOREIGN KEY (pharma_org_id) REFERENCES orgs(id),
                                            CONSTRAINT fk_report_cso FOREIGN KEY (cso_org_id) REFERENCES orgs(id),
                                            CONSTRAINT fk_report_target FOREIGN KEY (target_partner_id) REFERENCES cso_partners(id),
                                            CONSTRAINT fk_report_user FOREIGN KEY (report_user_id) REFERENCES users(id)
);

-- 보고 증빙 서류
CREATE TABLE cso_report_documents (
                                      id                       uuid                     DEFAULT uuid_generate_v4() PRIMARY KEY,
                                      re_entrustment_report_id uuid                     NOT NULL,
                                      doc_type                 varchar(30)              NOT NULL, -- BIZ_REG, CONTRACT, NOTICE, EDU
                                      s3file_id                uuid                     NOT NULL,
                                      deleted                  boolean                  DEFAULT false,
                                      modified_at              timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                                      created_at               timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                                      CONSTRAINT fk_doc_report_id FOREIGN KEY (re_entrustment_report_id) REFERENCES cso_re_entrustment_reports(id),
                                      CONSTRAINT fk_doc_s3file_id FOREIGN KEY (s3file_id) REFERENCES s3files(id)
);

-- 승인/반려 로그
CREATE TABLE cso_report_approval_logs (
                                          id          uuid                     DEFAULT uuid_generate_v4() PRIMARY KEY,
                                          report_id   uuid                     NOT NULL,
                                          act_user_id uuid                     NOT NULL,
                                          action_type varchar(20)              NOT NULL, -- SUBMIT, REJECT, RESUBMIT, APPROVE
                                          comment     text,
                                          deleted     boolean                  DEFAULT false,
                                          modified_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                                          created_at  timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                                          CONSTRAINT fk_log_report_id FOREIGN KEY (report_id) REFERENCES cso_re_entrustment_reports(id),
                                          CONSTRAINT fk_log_user_id FOREIGN KEY (act_user_id) REFERENCES users(id)
);


-- 게시판 (공지사항 등)
CREATE TABLE boards (
                        id          uuid                     DEFAULT uuid_generate_v4() PRIMARY KEY,
                        category    varchar(20)              NOT NULL, -- NOTICE, LAW, FAQ
                        title       varchar(200)             NOT NULL,
                        content     text                     NOT NULL,
                        is_fixed    boolean                  DEFAULT false,
                        deleted     boolean                  DEFAULT false,
                        modified_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
                        created_at  timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);

