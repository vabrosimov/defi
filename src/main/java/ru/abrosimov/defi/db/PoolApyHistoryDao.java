package ru.abrosimov.defi.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.abrosimov.defi.model.PoolApyHistory;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PoolApyHistoryDao {
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public void insert(List<PoolApyHistory> poolApyHistories) {
        String sql = """
                insert into pool_apy_history (project, chain, symbol, apy, update_ts)
                values (:project, :chain, :symbol, :apy, current_timestamp)
                """;

        SqlParameterSource[] batchParams = poolApyHistories.stream()
                .map(pool -> new MapSqlParameterSource()
                        .addValue("project", pool.getProject())
                        .addValue("chain", pool.getChain())
                        .addValue("symbol", pool.getSymbol())
                        .addValue("apy", roundApy(pool.getApy()))
                )
                .toArray(SqlParameterSource[]::new);

        long updateCount = Arrays.stream(namedJdbcTemplate.batchUpdate(sql, batchParams))
                .count();
        log.debug("Insert List<PoolApyHistory> to pool_apy_history table:\nUpdate count {}", updateCount);
    }

    private double roundApy(double apy) {
        return Math.round(apy * 100.0) / 100.0;
    }
}