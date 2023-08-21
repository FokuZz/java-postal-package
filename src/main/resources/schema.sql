CREATE TABLE IF NOT EXISTS users
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email        VARCHAR(64) NOT NULL,
    phone_number VARCHAR(12),
    name         VARCHAR(64) NOT NULL,
    last_name    VARCHAR(64) NOT NULL,
    CONSTRAINT UNI_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS post_office
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    postal_index varchar(6)   NOT NULL,
    name         VARCHAR(64)  NOT NULL,
    address      VARCHAR(256) NOT NULL,
    CONSTRAINT UNI_POST_OFFICE_NAME UNIQUE (name),
    CONSTRAINT UNI_POST_OFFICE_ADDRESS UNIQUE (address)
);

CREATE TABLE IF NOT EXISTS mail
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type_mail  VARCHAR(32)  NOT NULL,
    owner_id   BIGINT       NOT NULL,
    office_id  BIGINT       NOT NULL,
    mail_index varchar(6)   NOT NULL,
    address    varchar(256) NOT NULL,
    CONSTRAINT UNI_MAIL_INDEX UNIQUE (mail_index),
    FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (office_id) REFERENCES post_office (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS mail_history
(
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    mail_id BIGINT     NOT NULL,
    info    varchar(256) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL ,
    FOREIGN KEY (mail_id) REFERENCES mail (id) ON DELETE CASCADE
);


