package ru.abrosimov.defi.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.abrosimov.defi.db.PoolApyHistoryDao;
import ru.abrosimov.defi.db.PoolDao;
import ru.abrosimov.defi.model.Pool;
import ru.abrosimov.defi.model.PoolApyHistory;
import ru.abrosimov.defi.rest.DefiLamaClient;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PoolScrapper {
    private final DefiLamaClient defiLamaClient;
    private final PoolDao poolDao;
    private final PoolApyHistoryDao poolApyHistoryDao;

    @Value("${pool-scrapper-interval-hours}")
    private long poolScrapperIntervalHours;

    @Scheduled(fixedDelay = 10, initialDelay = 0, timeUnit = TimeUnit.MINUTES)
    public void scrapePools() {
        try {
            if (!shouldScrape()) {
                return;
            }

            JsonNode dataNode = defiLamaClient.getPools();

            List<Pool> filteredPools = filterPools(dataNode);
            filteredPools.sort(Comparator.comparing(Pool::getApy));

            log.info("Found {} pools matching criteria", filteredPools.size());

            poolDao.upsert(filteredPools);
            poolApyHistoryDao.insert(filteredPools.stream().map(this::toPoolApyHistory).toList());

        } catch (Exception e) {
            log.error("Unexpected error during pools scraping", e);
        }
    }

    @Scheduled(fixedDelay = 60, initialDelay = 60, timeUnit = TimeUnit.MINUTES)
    public void deleteOutdatedPools() {
        try {
            List<String> deletedPools = poolDao.deletePoolsOlderThanHours(poolScrapperIntervalHours * 2);
            poolApyHistoryDao.deletePoolApyHistoriesByPool(deletedPools);
        } catch (Exception e) {
            log.error("Unexpected error during deleting outdated pools", e);
        }
    }

    private boolean shouldScrape() {
        Instant lastUpdateTs = poolDao.getLastUpdateTs();

        if (lastUpdateTs == null) {
            return true;
        }

        long hoursSinceLastUpdate = Duration.between(lastUpdateTs, Instant.now()).toHours();

        return hoursSinceLastUpdate >= poolScrapperIntervalHours;
    }

    private List<Pool> filterPools(JsonNode dataNode) {
        List<Pool> filteredPools = new ArrayList<>();

        for (JsonNode item : dataNode) {
            if (isSymbolStringAndMatchPattern(item)) {
                filteredPools.add(toPool(item));
            }
        }

        return filteredPools;
    }

    private boolean isSymbolStringAndMatchPattern(JsonNode item) {
        JsonNode symbolNode = item.get("symbol");

        if (symbolNode == null || !symbolNode.isTextual()) {
            return false;
        }

        String symbol = symbolNode.asText();

        return symbol.toLowerCase().contains("eth") ||
                symbol.toLowerCase().contains("btc") ||
                symbol.toLowerCase().contains("sol");
    }

    private Pool toPool(JsonNode item) {
        return Pool.builder()
                .pool(item.path("pool").asText(""))
                .project(item.path("project").asText(""))
                .chain(item.path("chain").asText(""))
                .symbol(item.path("symbol").asText(""))
                .tvlUsd(item.path("tvlUsd").asLong(0))
                .ilRisk("yes".equalsIgnoreCase(item.path("ilRisk").asText(null)))
                .apyBase(item.path("apyBase").asDouble(0.0))
                .apyReward(item.path("apyReward").asDouble(0.0))
                .apy(item.path("apy").asDouble(0.0))
                .build();
    }

    private PoolApyHistory toPoolApyHistory(Pool pool) {
        return PoolApyHistory.builder()
                .pool(pool.getPool())
                .project(pool.getProject())
                .chain(pool.getChain())
                .symbol(pool.getSymbol())
                .apy(pool.getApy())
                .build();
    }
}

