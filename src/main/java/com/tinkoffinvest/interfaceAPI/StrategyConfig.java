package com.tinkoffinvest.interfaceAPI;

public interface StrategyConfig {
    boolean checkForOpenLong(String figi);
    boolean checkForCloseLong(String figi);
    boolean checkForOpenShort(String figi);
    boolean checkForCloseShort(String figi);
}
