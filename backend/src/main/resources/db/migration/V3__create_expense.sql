CREATE TABLE expense (
                         id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         amount          NUMERIC(19, 2) NOT NULL,
                         expense_date    DATE NOT NULL,
                         category_id     BIGINT NOT NULL,
                         payment_method  VARCHAR(20) NOT NULL,
                         merchant        VARCHAR(80),
                         note            VARCHAR(255),
                         created_at      TIMESTAMPTZ NOT NULL,
                         CONSTRAINT fk_expense_category FOREIGN KEY (category_id) REFERENCES category (id),
                         CONSTRAINT ck_expense_amount_positive CHECK (amount > 0),
                         CONSTRAINT ck_expense_payment_method
                             CHECK (payment_method IN ('CASH', 'DEBIT', 'CREDIT', 'TRANSFER', 'QR'))
);

CREATE INDEX ix_expense_date ON expense (expense_date);