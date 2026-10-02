ALTER TABLE category ADD COLUMN archived BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE category DROP CONSTRAINT uk_category_name;
CREATE UNIQUE INDEX uk_category_name_ci ON category (lower(name));