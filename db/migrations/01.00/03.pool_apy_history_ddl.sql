--liquibase formatted sql
--changeset Abrosimov V.R.:01.00.03

create table ${schemaName}.pool_apy_history (
    project varchar,
    chain varchar,
    symbol varchar,
    apy numeric,
    update_ts timestamp default now() not null
);

create index idx_pool_apy_history_project on ${schemaName}.pool_apy_history(project);
create index idx_pool_apy_history_chain on ${schemaName}.pool_apy_history(chain);
create index idx_pool_apy_history_symbol on ${schemaName}.pool_apy_history(symbol);
