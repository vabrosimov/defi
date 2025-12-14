package ru.abrosimov.defi.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Protocol {
    String name;
    String symbol;
    String url;
    String description;
    String chain;
    Integer audits;
    String category;
    String slug;
    Double tvl;
}
