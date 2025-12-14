--liquibase formatted sql
--changeset Abrosimov V.R.:01.00.01

create table ${schemaName}.pool (
    id bigserial primary key,
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

create index idx_pool_project on ${schemaName}.pool(project);
create index idx_pool_chain on ${schemaName}.pool(chain);
create index idx_pool_symbol on ${schemaName}.pool(symbol);
create index idx_pool_apy on ${schemaName}.pool(apy desc);

alter table ${schemaName}.pool
    add constraint uk_pool_project_chain_symbol
        unique (project, chain, symbol);