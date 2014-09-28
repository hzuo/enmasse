# --- !Ups

create table "job" ("id" BIGINT NOT NULL,"name" VARCHAR(254) NOT NULL,"data_origin" VARCHAR(254) NOT NULL,"map" VARCHAR(254) NOT NULL,"reduce" VARCHAR(254) NOT NULL,"created_at" BIGINT NOT NULL,"state" INTEGER NOT NULL);

alter table "job" add constraint "job_pk" primary key("id");

create table "file" ("job_id" BIGINT NOT NULL,"data" TEXT NOT NULL);

alter table "file" add constraint "file_fk_job" foreign key("job_id") references "job"("id") on update NO ACTION on delete NO ACTION;

create table "map_input" ("id" BIGINT NOT NULL,"k" VARCHAR(254) NOT NULL,"v" VARCHAR(254) NOT NULL,"job_id" BIGINT NOT NULL,"done" BOOLEAN NOT NULL);

alter table "map_input" add constraint "map_input_pk" primary key("id");

alter table "map_input" add constraint "map_input_fk_job" foreign key("job_id") references "job"("id") on update NO ACTION on delete NO ACTION;

create table "intermediate" ("id" BIGINT NOT NULL,"k" VARCHAR(254) NOT NULL,"v" VARCHAR(254) NOT NULL,"job_id" BIGINT NOT NULL,"done" BOOLEAN NOT NULL);

alter table "intermediate" add constraint "intermediate_pk" primary key("id");

alter table "intermediate" add constraint "intermediate_fk_job" foreign key("job_id") references "job"("id") on update NO ACTION on delete NO ACTION;

create table "reduce_output" ("id" BIGINT NOT NULL,"k" VARCHAR(254) NOT NULL,"v" VARCHAR(254) NOT NULL,"job_id" BIGINT NOT NULL);

alter table "reduce_output" add constraint "reduce_output_pk" primary key("id");

alter table "reduce_output" add constraint "reduce_output_fk_job" foreign key("job_id") references "job"("id") on update NO ACTION on delete NO ACTION;


# --- !Downs

alter table "reduce_output" drop constraint "reduce_output_fk_job";

alter table "reduce_output" drop constraint "reduce_output_pk";

drop table "reduce_output";

alter table "intermediate" drop constraint "intermediate_fk_job";

alter table "intermediate" drop constraint "intermediate_pk";

drop table "intermediate";

alter table "map_input" drop constraint "map_input_fk_job";

alter table "map_input" drop constraint "map_input_pk";

drop table "map_input";

alter table "file" drop constraint "file_fk_job";

drop table "file";

alter table "job" drop constraint "job_pk";

drop table "job";


