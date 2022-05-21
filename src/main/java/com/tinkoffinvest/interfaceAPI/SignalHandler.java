package com.tinkoffinvest.interfaceAPI;

import java.math.BigDecimal;

public interface SignalHandler {
    void handle(BigDecimal price, StrategyConfig config, String figi);
    void checkSignalForOpenLong(BigDecimal price, String figi, StrategyConfig config);
    void checkSignalForOpenShort(BigDecimal price, String figi, StrategyConfig config);
    void checkSignalForCloseLong(BigDecimal price, String figi, StrategyConfig config);
    void checkSignalForCloseShort(BigDecimal price, String figi, StrategyConfig config);
    void buyLong(BigDecimal price, String figi);
    void sellLong(BigDecimal price, String figi);
    void buyShort(BigDecimal price, String figi);
    void sellShort(BigDecimal price, String figi);
}
