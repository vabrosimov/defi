--liquibase formatted sql
--changeset Abrosimov V.R.:01.00.02

create table ${schemaName}.protocol (
    id bigserial primary key,
    name varchar not null,
    symbol varchar,
    url varchar,
    description text,
    chain varchar not null,
    audits numeric,
    category varchar,
    slug varchar,
    tvl numeric,
    update_ts timestamp default now() not null
);

create index idx_protocol_chain on ${schemaName}.protocol(chain);
create index idx_protocol_slug on ${schemaName}.protocol(slug);

alter table ${schemaName}.protocol
    add constraint uk_protocol_slug unique (slug);
