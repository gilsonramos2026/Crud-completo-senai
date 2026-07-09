-- 1. Insere o usuário administrador (Senha criptografada: admin123)
INSERT INTO users (id, username, email, password, enabled, created_at)
VALUES (1, 'admin', 'admin@email.com', '$2a$10$R77pOnp9v997NlyX7pQ1uO62F8u0yFjN98RzQW7vU67f5gR8z79qy', true, now())
ON CONFLICT (username) DO NOTHING;

-- 2. Vincula o usuário 'admin' à 'ROLE_ADMIN' (que possui ID 1 no banco)
INSERT INTO user_roles (user_id, role_id)
VALUES (1, 1)
ON CONFLICT DO NOTHING;

-- 3. Atualiza o sequencial do banco para evitar conflitos em cadastros futuros pelo Java
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
