-- Inserindo dados na tabela 'client'
INSERT INTO client (id, client_id, client_secret, redirect_uri, scope) VALUES ('550e8400-e29b-41d4-a716-446655440000', 'meuClient', '$2a$10$/yS0vgwkCm3h6PCMAwMA6eKMeqEuyEuU9hWjjjUjKVwjcYQBOEh.y', 'http://localhost:8080/authorized', 'read write');

-- Inserindo dados na tabela 'usuario'
INSERT INTO usuario (id, cns, senha, roles) VALUES ('550e8400-e29b-41d4-a716-446655440005', '123456789012345', '$2a$10$0Kxhiicu5MeLLeTFA4WosOqstc.czwhYNLqheZsQNVMseDi6xXfN.', ARRAY['PACIENTE']);