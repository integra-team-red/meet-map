ALTER TABLE users
    ADD COLUMN birth_date DATE,
    ADD COLUMN description VARCHAR(500);

UPDATE users
SET birth_date = '2000-01-01'
WHERE birth_date IS NULL;

ALTER TABLE users
    ALTER COLUMN birth_date SET NOT NULL;
