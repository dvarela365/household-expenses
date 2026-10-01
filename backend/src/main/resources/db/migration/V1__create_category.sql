CREATE TABLE category (
                          id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          name  VARCHAR(50) NOT NULL,
                          kind  VARCHAR(10) NOT NULL,
                          icon  VARCHAR(30),
                          CONSTRAINT uk_category_name UNIQUE (name),
                          CONSTRAINT ck_category_kind CHECK (kind IN ('FIXED', 'VARIABLE'))
);