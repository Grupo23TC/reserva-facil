-- client
INSERT INTO client (id, client_id, client_secret, redirect_uri, scope)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'meuClient', '$2a$10$/yS0vgwkCm3h6PCMAwMA6eKMeqEuyEuU9hWjjjUjKVwjcYQBOEh.y', 'http://localhost:8080/authorized', 'read write');

-- usuario 1
INSERT INTO usuario (id, cns, senha, ativo)
VALUES ('550e8400-e29b-41d4-a716-446655440005', '123456789012345', '$2a$10$0Kxhiicu5MeLLeTFA4WosOqstc.czwhYNLqheZsQNVMseDi6xXfN.', true);

-- usuario 2
INSERT INTO usuario (id, cns, senha, ativo)
VALUES ('550e8400-e29b-41d4-a716-446655440006', '234567890123456', '$2a$10$0Kxhiicu5MeLLeTFA4WosOqstc.czwhYNLqheZsQNVMseDi6xXfN.', true);

-- role
INSERT INTO role (id, authority)
VALUES ('550e8400-e29b-41d4-a716-446655440010', 'PACIENTE');

-- role
INSERT INTO role (id, authority)
VALUES ('550e8400-e29b-41d4-a716-446655440011', 'OPERADOR');

-- Associando usuário à role na tabela 'usuario_role'
INSERT INTO usuario_role (user_id, role_id)
VALUES ('550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440010');

-- Associando usuário à role na tabela 'usuario_role'
INSERT INTO usuario_role (user_id, role_id)
VALUES ('550e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440011');

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

INSERT INTO prestador (
    id,
    nome,
    nome_fantasia,
    endereco_id,
    tipo_prestador
) VALUES (
    '550e8400-e29b-41d4-a716-446655440000',
    'Farmácia Central',
    'Central Farma',
    '550e8400-e29b-41d4-a716-446655440001',
    'FARMACIA'
 );

INSERT INTO operador (
    cns,
    nome,
    cargo,
    usuario_id,
    prestador_id,
    ativo
) VALUES (
    '234567890123456',
    'João Silva',
    'Farmacêutico',
    '550e8400-e29b-41d4-a716-446655440006',
    '550e8400-e29b-41d4-a716-446655440000',
    true
);