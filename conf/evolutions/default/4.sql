# --- !Ups
CREATE SCHEMA victorine;

create table "victorine"."questions" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "group" INTEGER NOT NULL,
    "question" VARCHAR NOT NULL,
    "rightAnswer" INTEGER NOT NULL,
    "answers" VARCHAR NOT NULL
    );
create table "victorine"."question_qrs" (
    "id" SERIAL NOT NULL PRIMARY KEY,
    "qr" VARCHAR NOT NULL,
    "questionId" BIGINT NOT NULL
    );

create table "victorine"."question_infos" (
    "id" BIGSERIAL NOT NULL PRIMARY KEY,
    "questionId" BIGINT NOT NULL,
    "title" VARCHAR NOT NULL,
    "text" VARCHAR NOT NULL,
    "image" VARCHAR NOT NULL
    );
create table "victorine"."question_groups" (
    "id" SERIAL NOT NULL PRIMARY KEY,
    "title" VARCHAR NOT NULL,
    "description" VARCHAR NOT NULL,
    "color" INTEGER NOT NULL,
    "questionNamePrefix" VARCHAR NOT NULL
    );


# --- !Downs

drop table "victorine"."question_groups";
drop table "victorine"."question_infos";
drop table "victorine"."question_qrs";
drop table "victorine"."questions";

DROP SCHEMA victorine;