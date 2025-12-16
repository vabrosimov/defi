package ru.abrosimov.defi.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.abrosimov.defi.model.Pool;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PoolDao {
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public void upsert(List<Pool> pools) {
        String sql = """
                insert into pool (pool, project, chain, symbol, tvlUsd, ilRisk, apy_base, apy_reward, apy, update_ts)
                values (:pool, :project, :chain, :symbol, :tvlUsd, :ilRisk, :apy_base, :apy_reward, :apy, current_timestamp)
                on conflict (pool)
                do update set
                    project = excluded.project,
                    chain = excluded.chain,
                    symbol = excluded.symbol,
                    tvlUsd = excluded.tvlUsd,
                    ilRisk = excluded.ilRisk,
                    apy_base = excluded.apy_base,
                    apy_reward = excluded.apy_reward,
                    apy = excluded.apy,
                    update_ts = current_timestamp
                """;

        SqlParameterSource[] batchParams = pools.stream()
                .map(pool -> new MapSqlParameterSource()
                        .addValue("pool", pool.getPool())
                        .addValue("project", pool.getProject())
                        .addValue("chain", pool.getChain())
                        .addValue("symbol", pool.getSymbol())
                        .addValue("tvlUsd", roundTvl(pool.getTvlUsd()))
                        .addValue("ilRisk", pool.getIlRisk())
                        .addValue("apy_base", roundApy(pool.getApyBase()))
                        .addValue("apy_reward", roundApy(pool.getApyReward()))
                        .addValue("apy", roundApy(pool.getApy()))
                )
                .toArray(SqlParameterSource[]::new);

        long updateCount = Arrays.stream(namedJdbcTemplate.batchUpdate(sql, batchParams))
                .count();
        log.debug("Upsert List<Pool> to pool table:\nUpdate count {}", updateCount);
    }

    private double roundTvl(double tvlUsd) {
        return Math.round((tvlUsd / 1_000_000.0) * 1000.0) / 1000.0;
    }

    private double roundApy(double apy) {
        return Math.round(apy * 100.0) / 100.0;
    }
}