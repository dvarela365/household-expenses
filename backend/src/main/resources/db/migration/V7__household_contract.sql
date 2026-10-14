-- Defensa: si quedó alguna fila sin hogar (creada entre la V6 y el código nuevo),
-- se asigna al hogar inicial antes de exigir el NOT NULL.
UPDATE category SET household_id = (SELECT min(id) FROM household) WHERE household_id IS NULL;
UPDATE expense  SET household_id = (SELECT min(id) FROM household) WHERE household_id IS NULL;

ALTER TABLE category ALTER COLUMN household_id SET NOT NULL;
ALTER TABLE expense  ALTER COLUMN household_id SET NOT NULL;

-- El nombre de categoría pasa de ser único global a único por hogar.
DROP INDEX uk_category_name_ci;
CREATE UNIQUE INDEX uk_category_household_name_ci ON category (household_id, lower(name));