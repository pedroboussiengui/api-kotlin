PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS users_tb (
    id INTEGER PRIMARY KEY,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    email TEXT NOT NULL,
    cep TEXT,
    rua TEXT,
    numero INTEGER,
    bairro TEXT,
    cidade TEXT,
    estado TEXT
);

CREATE TABLE IF NOT EXISTS posts_tb (
    id INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    timestamp TEXT NOT NULL,
    likes INTEGER NOT NULL,
    is_private INTEGER NOT NULL,
    user_id INTEGER,
    FOREIGN KEY (user_id) REFERENCES users_tb (id)
);