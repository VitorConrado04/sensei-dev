-- SQL dump generated using DBML (dbml.dbdiagram.io)
-- Database: PostgreSQL
-- Generated at: 2026-09-07T20:52:28.019Z

CREATE TABLE "usuario" (
  "id" int PRIMARY KEY,
  "nome" varchar,
  "email" varchar,
  "senha" varchar,
  "semestre_atual" int,
  "foto_url" varchar,
  "status" varchar,
  "criado_em" timestamp
);

CREATE TABLE "disciplina" (
  "id" int PRIMARY KEY,
  "nome" varchar,
  "semestre" int
);

CREATE TABLE "mentor_perfil" (
  "id" int PRIMARY KEY,
  "descricao" varchar,
  "experiencia" varchar,
  "nota_media" decimal,
  "total_mentorias" int
);

CREATE TABLE "mentorando_perfil" (
  "id" int PRIMARY KEY,
  "criado_em" timestamp
);

CREATE TABLE "mentor_disciplina" (
  "mentor_id" int,
  "disciplina_id" int,
  PRIMARY KEY ("mentor_id", "disciplina_id")
);

CREATE TABLE "mentorando_disciplina_interesse" (
  "mentorando_id" int,
  "disciplina_id" int,
  PRIMARY KEY ("mentorando_id", "disciplina_id")
);

CREATE TABLE "favorito" (
  "mentorando_id" int,
  "mentor_id" int,
  "criado_em" timestamp,
  PRIMARY KEY ("mentorando_id", "mentor_id")
);

CREATE TABLE "disponibilidade" (
  "id" int PRIMARY KEY,
  "mentor_id" int,
  "dia_semana" int,
  "hora_inicio" varchar,
  "hora_fim" varchar,
  "ativo" boolean
);

CREATE TABLE "bloqueio_horario" (
  "id" int PRIMARY KEY,
  "mentor_id" int,
  "data" date,
  "hora_inicio" varchar,
  "hora_fim" varchar,
  "motivo" varchar
);

CREATE TABLE "mentoria" (
  "id" int PRIMARY KEY,
  "mentor_id" int,
  "mentorando_id" int,
  "disciplina_id" int,
  "mentoria_origem_id" int,
  "objetivo" varchar,
  "data_hora_inicio" timestamp,
  "data_hora_fim" timestamp,
  "status" varchar,
  "criado_em" timestamp,
  "atualizado_em" timestamp
);

CREATE TABLE "avaliacao" (
  "id" int PRIMARY KEY,
  "mentoria_id" int UNIQUE,
  "clareza" int,
  "dominio_assunto" int,
  "pontualidade" int,
  "capacidade_orientar" int,
  "experiencia_geral" int,
  "nota_final" decimal,
  "comentario" varchar,
  "criado_em" timestamp
);

CREATE TABLE "material" (
  "id" int PRIMARY KEY,
  "mentor_id" int,
  "mentoria_id" int,
  "titulo" varchar,
  "descricao" varchar,
  "arquivo_url" varchar,
  "criado_em" timestamp
);

CREATE TABLE "notificacao" (
  "id" int PRIMARY KEY,
  "usuario_id" int,
  "mentoria_id" int,
  "tipo" varchar,
  "mensagem" varchar,
  "lida" boolean,
  "criado_em" timestamp
);

ALTER TABLE "mentor_perfil" ADD FOREIGN KEY ("id") REFERENCES "usuario" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "mentorando_perfil" ADD FOREIGN KEY ("id") REFERENCES "usuario" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "mentor_disciplina" ADD FOREIGN KEY ("mentor_id") REFERENCES "mentor_perfil" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "mentor_disciplina" ADD FOREIGN KEY ("disciplina_id") REFERENCES "disciplina" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "mentorando_disciplina_interesse" ADD FOREIGN KEY ("mentorando_id") REFERENCES "mentorando_perfil" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "mentorando_disciplina_interesse" ADD FOREIGN KEY ("disciplina_id") REFERENCES "disciplina" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "favorito" ADD FOREIGN KEY ("mentorando_id") REFERENCES "mentorando_perfil" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "favorito" ADD FOREIGN KEY ("mentor_id") REFERENCES "mentor_perfil" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "disponibilidade" ADD FOREIGN KEY ("mentor_id") REFERENCES "mentor_perfil" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "bloqueio_horario" ADD FOREIGN KEY ("mentor_id") REFERENCES "mentor_perfil" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "mentoria" ADD FOREIGN KEY ("mentor_id") REFERENCES "mentor_perfil" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "mentoria" ADD FOREIGN KEY ("mentorando_id") REFERENCES "mentorando_perfil" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "mentoria" ADD FOREIGN KEY ("disciplina_id") REFERENCES "disciplina" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "mentoria" ADD FOREIGN KEY ("mentoria_origem_id") REFERENCES "mentoria" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "avaliacao" ADD FOREIGN KEY ("mentoria_id") REFERENCES "mentoria" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "material" ADD FOREIGN KEY ("mentor_id") REFERENCES "mentor_perfil" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "material" ADD FOREIGN KEY ("mentoria_id") REFERENCES "mentoria" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "notificacao" ADD FOREIGN KEY ("usuario_id") REFERENCES "usuario" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "notificacao" ADD FOREIGN KEY ("mentoria_id") REFERENCES "mentoria" ("id") DEFERRABLE INITIALLY IMMEDIATE;
