--liquibase formatted sql
--changeset Abrosimov V.R.:01.00.01

create table pool (
    pool varchar primary key,
    project varchar,
    chain varchar,
    symbol varchar,
    tvlUsd numeric,
    ilRisk boolean,
    apy_base numeric,
    apy_reward numeric,
    apy numeric,
    update_ts timestamp default now() not null
);

create index idx_pool_project on pool(project);
create index idx_pool_chain on pool(chain);
create index idx_pool_symbol on pool(symbol);
create index idx_pool_apy on pool(apy desc);
