package com.tinkoffinvest.simulator;

import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

import java.util.List;

import com.tinkoffinvest.cache.CandlesCache;
import com.tinkoffinvest.cache.RSICache;
import com.tinkoffinvest.model.CachedCandle;
import com.tinkoffinvest.model.RSIStrategyConfig;
import com.tinkoffinvest.signal.RSISignalHandler;

@RequiredArgsConstructor
public class StrategySimulator {

    private final CandlesCache candlesCache;
    private final RSISignalHandler signalHandler;
    private final RSICache rsiCacheService;

    public void simulate(List<RSIStrategyConfig> configs) {
        candlesCache.collectHistoricalCandles(configs);
        var candles = candlesCache.getCache();
        for (RSIStrategyConfig config : configs) {
            for (String figi : config.getFigi()) {
                for (CachedCandle cachedCandle : candles.get(figi)) {
                    rsiCacheService.calculateRSI(figi, candles, config);
                    signalHandler.handle(cachedCandle.getClosePrice(), config, figi);
                }
            }
        }

    }
}
