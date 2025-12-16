package ru.abrosimov.defi.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Pool {
    String pool;
    String project;
    String chain;
    String symbol;
    long tvlUsd;
    Boolean ilRisk;
    double apyBase;
    double apyReward;
    double apy;
}
