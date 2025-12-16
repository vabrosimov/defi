--liquibase formatted sql
--changeset Abrosimov V.R.:01.00.03

create table pool_apy_history (
    pool varchar,
    project varchar,
    chain varchar,
    symbol varchar,
    apy numeric,
    update_ts timestamp default now() not null
);

create index idx_pool_apy_history_project on pool_apy_history(project);
create index idx_pool_apy_history_chain on pool_apy_history(chain);
create index idx_pool_apy_history_symbol on pool_apy_history(symbol);
