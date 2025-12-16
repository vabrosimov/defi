--liquibase formatted sql
--changeset Abrosimov V.R.:01.00.04

create view pool_with_apy_history as
select
    pool.project,
    pool.chain,
    pool.symbol,
    pool.apy,
    array_agg(h.apy order by h.update_ts) as apy_history,
    pool.tvlusd,
    p.tvl,
    p.url
from pool
         left join defi.protocol p
                   on pool.project = p.slug
         left join pool_apy_history h
                   on h.pool = pool.pool
where pool.ilrisk = false
  and pool.tvlusd > 0.1
  and pool.apy > 0
group by
    pool.project,
    pool.chain,
    pool.symbol,
    pool.apy,
    pool.tvlusd,
    p.tvl,
    p.url;
