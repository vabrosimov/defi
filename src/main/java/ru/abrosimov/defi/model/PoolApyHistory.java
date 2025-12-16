package ru.abrosimov.defi.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PoolApyHistory {
    String pool;
    String project;
    String chain;
    String symbol;
    double apy;
}
