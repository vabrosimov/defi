package ru.abrosimov.defi.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.abrosimov.defi.model.Protocol;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProtocolDao {
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public void upsert(List<Protocol> protocols) {
        String sql = """
            insert into protocol (name, symbol, url, description, chain, audits, category, slug, tvl, update_ts)
            values (:name, :symbol, :url, :description, :chain, :audits, :category, :slug, :tvl, current_timestamp)
            on conflict (slug)
            do update set
                name = excluded.name,
                symbol = excluded.symbol,
                url = excluded.url,
                description = excluded.description,
                chain = excluded.chain,
                audits = excluded.audits,
                category = excluded.category,
                tvl = excluded.tvl,
                update_ts = current_timestamp
            """;

        SqlParameterSource[] batchParams = protocols.stream()
                .map(protocol -> new MapSqlParameterSource()
                        .addValue("name", protocol.getName())
                        .addValue("symbol", protocol.getSymbol())
                        .addValue("url", protocol.getUrl())
                        .addValue("description", protocol.getDescription())
                        .addValue("chain", protocol.getChain())
                        .addValue("audits", protocol.getAudits())
                        .addValue("category", protocol.getCategory())
                        .addValue("slug", protocol.getSlug())
                        .addValue("tvl", protocol.getTvl() != null ? roundTvl(protocol.getTvl()) : null)
                )
                .toArray(SqlParameterSource[]::new);

        long updateCount = Arrays.stream(namedJdbcTemplate.batchUpdate(sql, batchParams))
                .count();
        log.debug("Upsert List<Protocol> to protocol table:\nUpdate count {}", updateCount);
    }

    private double roundTvl(double tvlUsd) {
        return Math.round((tvlUsd / 1_000_000.0) * 1000.0) / 1000.0;
    }
}
