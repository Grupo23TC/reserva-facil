-- client
INSERT INTO client (id, client_id, client_secret, redirect_uri, scope)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'meuClient', '$2a$10$/yS0vgwkCm3h6PCMAwMA6eKMeqEuyEuU9hWjjjUjKVwjcYQBOEh.y', 'http://localhost:8080/authorized', 'read write');

-- usuario
INSERT INTO usuario (id, cns, senha, ativo)
VALUES ('550e8400-e29b-41d4-a716-446655440005', '123456789012345', '$2a$10$0Kxhiicu5MeLLeTFA4WosOqstc.czwhYNLqheZsQNVMseDi6xXfN.', false);

-- role
INSERT INTO role (id, authority)
VALUES ('550e8400-e29b-41d4-a716-446655440010', 'PACIENTE');

-- Associando usuário à role na tabela 'usuario_role'
INSERT INTO usuario_role (user_id, role_id)
VALUES ('550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440010');

-- endereco
INSERT INTO endereco (id, logradouro, bairro, cidade, estado, complemento)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'Rua das Flores', 'Centro', 'São Paulo', 'SP', 'Sala 101');

-- beneficiario
INSERT INTO beneficiario (
    cns,
    nome,
    cpf,
    telefone,
    faixa_etaria,
    genero,
    tipo_medicamento,
    ativo,
    endereco_id,
    usuario_id
) VALUES (
    '123456789012345',
    'Fulano da Silva',
    '01234567890',
    '11999999999',
    'FAIXA_0_a_18',
    'OUTROS',
    'ESPECIAIS',
    true,
    '550e8400-e29b-41d4-a716-446655440001',
    '550e8400-e29b-41d4-a716-446655440005'
);
