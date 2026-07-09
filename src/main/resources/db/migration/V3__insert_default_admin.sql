-- 1. Insere o usuário apenas se o 'username' já não existir (evita erro de ID)
INSERT INTO users (username, email, password, enabled, created_at)
VALUES ('admin', 'admin@email.com', '$2a$10$R77pOnp9v997NlyX7pQ1uO62F8u0yFjN98RzQW7vU67f5gR8z79qy', true, now())
ON CONFLICT (username) DO NOTHING;

-- 2. Vincula o usuário 'admin' à 'ROLE_ADMIN' buscando os IDs de forma dinâmica
INSERT INTO user_roles (user_id, role_id)
VALUES (
    (SELECT id FROM users WHERE username = 'admin'),
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
)
ON CONFLICT DO NOTHING;
