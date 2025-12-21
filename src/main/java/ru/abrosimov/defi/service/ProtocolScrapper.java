package ru.abrosimov.defi.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.abrosimov.defi.db.ProtocolDao;
import ru.abrosimov.defi.model.Protocol;
import ru.abrosimov.defi.rest.DefiLamaClient;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProtocolScrapper {
    private final DefiLamaClient defiLamaClient;
    private final ProtocolDao protocolDao;

    @Value("${protocol-scrapper-interval-hours}")
    private long protocolScrapperIntervalHours;

    @Scheduled(fixedDelay = 10, initialDelay = 0, timeUnit = TimeUnit.MINUTES)
    public void scrapeProtocols() {
        try {
            if (!shouldScrape()) {
                return;
            }

            JsonNode protocolsDataNode = defiLamaClient.getProtocols();

            List<Protocol> filteredPools = filterProtocols(protocolsDataNode);

            log.info("Found {} protocols matching criteria", filteredPools.size());

            protocolDao.upsert(filteredPools);

        } catch (Exception e) {
            log.error("Unexpected error during pools scraping", e);
        }
    }

    @Scheduled(fixedDelay = 60, initialDelay = 60, timeUnit = TimeUnit.MINUTES)
    public void deleteOutdatedPools() {
        try {
            protocolDao.deleteProtocolsOlderThanHours(protocolScrapperIntervalHours + 2);

        } catch (Exception e) {
            log.error("Unexpected error during deleting outdated protocols", e);
        }
    }

    private boolean shouldScrape() {
        Instant lastUpdateTs = protocolDao.getLastUpdateTs();

        if (lastUpdateTs == null) {
            return true;
        }

        long hoursSinceLastUpdate = Duration.between(lastUpdateTs, Instant.now()).toHours();

        return hoursSinceLastUpdate >= protocolScrapperIntervalHours;
    }

    private List<Protocol> filterProtocols(JsonNode dataNode) {
        List<Protocol> filteredProtocols = new ArrayList<>();

        for (JsonNode item : dataNode) {
            filteredProtocols.add(toProtocol(item));
        }

        return filteredProtocols;
    }

    private Protocol toProtocol(JsonNode item) {
        return Protocol.builder()
                .name(item.path("name").asText(""))
                .symbol(item.path("symbol").asText(""))
                .url(item.path("url").asText(""))
                .description(item.path("description").asText(""))
                .chain(item.path("chain").asText(""))
                .audits(item.path("audits").isNumber() ? item.path("audits").asInt() : null)
                .category(item.path("category").asText(null))
                .slug(item.path("slug").asText(null))
                .tvl(item.path("tvl").isNumber() ? item.path("tvl").asDouble() : null)
                .build();
    }
}

