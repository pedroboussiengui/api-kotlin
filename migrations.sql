--PRAGMA foreign_keys = ON;
--
--CREATE TABLE IF NOT EXISTS users_tb (
--    id INTEGER PRIMARY KEY,
--    username TEXT NOT NULL,
--    password TEXT NOT NULL,
--    email TEXT NOT NULL,
--    cep TEXT,
--    rua TEXT,
--    numero INTEGER,
--    bairro TEXT,
--    cidade TEXT,
--    estado TEXT
--);
--
--CREATE TABLE IF NOT EXISTS posts_tb (
--    id INTEGER PRIMARY KEY,
--    title TEXT NOT NULL,
--    content TEXT NOT NULL,
--    timestamp TEXT NOT NULL,
--    likes INTEGER NOT NULL,
--    is_private INTEGER NOT NULL,
--    user_id INTEGER,
--    FOREIGN KEY (user_id) REFERENCES users_tb (id)
--);

ALTER TABLE users_tb RENAME COLUMN cep TO address_cep;
ALTER TABLE users_tb RENAME COLUMN rua TO address_rua;
ALTER TABLE users_tb RENAME COLUMN numero TO address_numero;
ALTER TABLE users_tb RENAME COLUMN bairro TO address_bairro;
ALTER TABLE users_tb RENAME COLUMN cidade TO address_cidade;
ALTER TABLE users_tb RENAME COLUMN estado TO address_estado;