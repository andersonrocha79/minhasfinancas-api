
-- DROP DATABASE minhasfinancas;

CREATE DATABASE minhasfinancas;

CREATE SCHEMA financas;

CREATE TABLE financas.usuario
(
  id bigserial NOT NULL PRIMARY KEY,
  nome character varying(150),
  email character varying(100),
  senha character varying(20),
  data_cadastro date default now()
);

CREATE TABLE financas.lancamento
(
  id bigserial NOT NULL PRIMARY KEY ,
  descricao character varying(100) NOT NULL,
  mes integer NOT NULL,
  ano integer NOT NULL,
  valor numeric(16,2) NOT NULL,
  tipo character varying(20) NOT NULL CHECK ( tipo in ('RECEITA', 'DESPESA')),
  status character varying(20) NOT NULL CHECK ( status in ('PENDENTE', 'CANCELADO', 'EFETIVADO')),
  id_usuario bigint NOT NULL REFERENCES financas.usuario (id),
  data_cadastro date NOT NULL default now()
);