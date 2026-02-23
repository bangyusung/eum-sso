CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE orgs (
                      id SERIAL PRIMARY KEY,
                      org_type VARCHAR(10) NOT NULL CHECK (org_type IN ('PHARMA', 'CSO')), -- 제약사, CSO 구분 [cite: 25]
                      biz_name VARCHAR(210) NOT NULL, -- 사업자명 [cite: 2, 65]
                      biz_number VARCHAR(20) NOT NULL UNIQUE, -- 사업자번호 [cite: 2, 65]
                      rep_name VARCHAR(50) NOT NULL, -- 대표자명 [cite: 2, 65]
                      address TEXT NOT NULL, -- 주소 [cite: 2, 65]
                      biz_doc_url TEXT, -- 사업자등록증 S3 URL [cite: 2, 66]
                      status VARCHAR(20) DEFAULT 'NORMAL' CHECK (status IN ('NORMAL', 'CLOSED', 'SUSPENDED')), -- 정상, 폐업, 정지 [cite: 9, 82]
                      edu_completion_date DATE, -- CSO 교육 수료일자 [cite: 9, 65]
                      deleted BOOLEAN DEFAULT FALSE,
                      modified_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                      created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     account VARCHAR(50) NOT NULL UNIQUE, -- 로그인 아이디 [cite: 65]
                                     org_id INTEGER NOT NULL REFERENCES orgs(id), -- 소속 조직
                                     username VARCHAR(50) NOT NULL, -- 담당자명 [cite: 2, 65]
                                     password VARCHAR(255) NOT NULL, -- 해싱된 비밀번호 [cite: 5, 79]
                                     email VARCHAR(100) NOT NULL, -- 이메일 [cite: 2, 65]
                                     dept_name VARCHAR(100), -- 부서명 [cite: 2, 59]
                                     phone_number VARCHAR(20) NOT NULL, -- 연락처 [cite: 2, 65]
                                     locked BOOLEAN DEFAULT FALSE, -- 계정 잠금 여부 [cite: 5, 72]
                                     status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('APPROVED', 'PENDING')), -- 가입 승인 상태 [cite: 69, 158]
                                     user_role VARCHAR(20) DEFAULT 'STAFF'::character varying not null,
                                     last_login_at TIMESTAMPTZ,
                                     deleted BOOLEAN DEFAULT FALSE,
                                     modified_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                     created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE
);

COMMENT ON TABLE roles IS '사용자 권한(역할) 정의 테이블';

CREATE TABLE IF NOT EXISTS users_roles (
                                           user_id SERIAL NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                           role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
                                           PRIMARY KEY (user_id, role_id)
);

COMMENT ON TABLE users_roles IS '사용자(UUID 기반)-권한 매핑 테이블';

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
                                                   modified_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                                   CONSTRAINT fk_client_info FOREIGN KEY (client_id)
                                                       REFERENCES oauth2_registered_client(client_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS oauth2_authorization (
                                                    id varchar(100) NOT NULL PRIMARY KEY,
                                                    registered_client_id varchar(100) NOT NULL,
                                                    principal_name varchar(200) NOT NULL,
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

INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN') ON CONFLICT DO NOTHING;

CREATE TABLE terms_and_agreements (
                                      id SERIAL PRIMARY KEY,
                                      user_id INTEGER NOT NULL REFERENCES users(id),
                                      terms_version VARCHAR(20) NOT NULL, -- 약관 버전 [cite: 2, 67]
                                      is_agreed BOOLEAN NOT NULL DEFAULT FALSE, -- 동의 여부 [cite: 2, 67]
                                      agreed_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                      deleted BOOLEAN DEFAULT FALSE,
                                      modified_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                      created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE csos (
                      id SERIAL PRIMARY KEY,
                      entrustment_org_id INTEGER NOT NULL REFERENCES orgs(id), -- 위탁자(제약사/상위CSO) [cite: 18, 101]
                      org_id INTEGER REFERENCES orgs(id), -- 회원가입 시 매핑될 조직 ID [cite: 21, 23, 87]
                      biz_name VARCHAR(210) NOT NULL, -- 업체명 [cite: 21, 82, 92]
                      biz_number VARCHAR(20) NOT NULL, -- 사업자번호 [cite: 21, 82, 92]
                      rep_name VARCHAR(50) NOT NULL, -- 대표자명 [cite: 21, 82, 92]
                      address TEXT NOT NULL, -- 주소 [cite: 21, 92]
                      biz_reg_s3file_id INTEGER, -- 사업자등록증 파일 [cite: 10, 92]
                      notice_s3file_id INTEGER, -- CSO 신고증 파일 [cite: 10, 92]
                      edu_s3file_id INTEGER, -- 교육수료증 파일 [cite: 10, 92]
                      medical_device_s3file_id INTEGER, -- 의료기기 신고증 [cite: 11, 22]
                      status VARCHAR(20) DEFAULT 'NORMAL' CHECK (status IN ('NORMAL', 'CLOSED', 'SUSPENDED')), -- 업체 상태 [cite: 9, 82]
                      edu_completion_date DATE, -- 교육 수료일 [cite: 9, 21, 82]
                      deleted BOOLEAN DEFAULT FALSE,
                      modified_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                      created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cso_contracts (
                               id SERIAL PRIMARY KEY,
                               cso_id INTEGER NOT NULL REFERENCES csos(id), -- 거래처 참조 [cite: 95]
                               contract_s3file_id INTEGER NOT NULL, -- 계약서 파일 ID [cite: 96, 99]
                               contract_start_date DATE NOT NULL, -- 계약 시작일 [cite: 97]
                               contract_end_date DATE NOT NULL, -- 계약 종료일 [cite: 97]
                               commission_info JSONB, -- 수수료율 등 상세 정보 (PostgreSQL JSONB 활용) [cite: 98, 99]
                               auto_renewal BOOLEAN DEFAULT FALSE, -- 자동 갱신 여부 [cite: 98]
                               deleted BOOLEAN DEFAULT FALSE,
                               modified_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                               created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cso_re_entrustment_reports (
                                            id SERIAL PRIMARY KEY,
                                            pharma_org_id INTEGER NOT NULL REFERENCES orgs(id), -- 최종 제약사 [cite: 12, 117, 131]
                                            entrustment_cso_id INTEGER NOT NULL REFERENCES csos(id), -- 위탁 CSO [cite: 27, 105]
                                            target_cso_id INTEGER NOT NULL REFERENCES csos(id), -- 수탁 CSO (보고 대상) [cite: 27, 105]
                                            approval_status VARCHAR(20) DEFAULT 'READY' CHECK (approval_status IN ('READY', 'APPROVED', 'REJECTED')), -- 승인 상태 [cite: 27, 106, 133]
                                            delivery_status VARCHAR(20) DEFAULT 'PROGRESS' CHECK (delivery_status IN ('DELIVERED', 'PROGRESS', 'FAILED')), -- 전송 상태 [cite: 133, 137]
                                            notice_date DATE NOT NULL, -- 통보서 작성 일자 [cite: 31, 131]
                                            report_date DATE, -- 실제 보고 완료 일자 [cite: 28, 107]
                                            report_user_id INTEGER NOT NULL REFERENCES users(id), -- 보고자 [cite: 156]
                                            deleted BOOLEAN DEFAULT FALSE,
                                            modified_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                            created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cso_report_approval_logs (
                                          id SERIAL PRIMARY KEY,
                                          report_id INTEGER NOT NULL REFERENCES cso_re_entrustment_reports(id),
                                          act_user_id INTEGER NOT NULL REFERENCES users(id), -- 처리자 [cite: 127]
                                          action_type VARCHAR(20) NOT NULL CHECK (action_type IN ('SUBMIT', 'REJECT', 'RESUBMIT', 'APPROVE')), -- 액션 유형 [cite: 125]
                                          comment TEXT, -- 반려 사유 등 코멘트 [cite: 39, 126]
                                          deleted BOOLEAN DEFAULT FALSE,
                                          modified_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                          created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE s3files (
                         id SERIAL PRIMARY KEY,
                         url TEXT NOT NULL, -- S3 URL
                         file_name VARCHAR(255), -- 파일명 [cite: 95]
                         file_size BIGINT NOT NULL, -- 용량
                         uploaded_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                         deleted BOOLEAN DEFAULT FALSE,
                         modified_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                         created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE boards (
                        id SERIAL PRIMARY KEY,
                        category VARCHAR(20) NOT NULL CHECK (category IN ('NOTICE', 'LAW', 'FAQ')), -- 카테고리 [cite: 111, 112]
                        title VARCHAR(255) NOT NULL, -- 제목 [cite: 113]
                        content TEXT NOT NULL, -- 내용 [cite: 114]
                        is_fixed BOOLEAN DEFAULT FALSE, -- 상단 고정 [cite: 115]
                        deleted BOOLEAN DEFAULT FALSE,
                        modified_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                        created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);