# --- !Ups

create table "job" ("id" BIGINT NOT NULL,"data" TEXT NOT NULL);

create table "map_input" ("id" BIGINT NOT NULL,"k" VARCHAR(254) NOT NULL,"v" VARCHAR(254) NOT NULL,"job_id" BIGINT NOT NULL,"done" BOOLEAN NOT NULL);

create table "intermediate" ("id" BIGINT NOT NULL,"k" VARCHAR(254) NOT NULL,"v" VARCHAR(254) NOT NULL,"job_id" BIGINT NOT NULL,"done" BOOLEAN NOT NULL);

create table "reduce_output" ("id" BIGINT NOT NULL,"k" VARCHAR(254) NOT NULL,"v" VARCHAR(254) NOT NULL,"job_id" BIGINT NOT NULL);


# --- !Downs

drop table "reduce_output";

drop table "intermediate";

drop table "map_input";

drop table "job";


