package com.tinkoffinvest.signal;

import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import com.tinkoffinvest.cache.OrdersCache;
import com.tinkoffinvest.interfaceAPI.SignalHandler;
import com.tinkoffinvest.interfaceAPI.StrategyConfig;
import com.tinkoffinvest.model.RSIStrategyConfig;
import com.tinkoffinvest.service.OrderService;

// @Service
@RequiredArgsConstructor
public class RSISignalHandler implements SignalHandler{

    private final OrderService orderService;
    private final OrdersCache ordersCache;

    @Override
    public void handle(BigDecimal price, StrategyConfig config, String figi) {
        checkSignalForOpenShort(price, figi, config);
        checkSignalForOpenLong(price, figi, config);
        checkSignalForCloseShort(price, figi, config);
        checkSignalForCloseLong(price, figi, config);
    }
    
    @Override
    public void checkSignalForOpenLong(BigDecimal price, String figi, StrategyConfig config) {
        RSIStrategyConfig rsiconfig = (RSIStrategyConfig) config;
        // открываем лонг, если RSI < lower (30)
        if (!rsiconfig.checkForOpenLong(figi)) {
            return;
        }
        //Если уже куплен в лонг - выходим
        if (ordersCache.longOpen(figi)) {
            return;
        }
        
        //закрываем шорт, если есть
        if (ordersCache.shortOpen(figi)) {
            sellShort(price, figi);
        }
        
        buyLong(price, figi);
    }
    
    @Override
    public void checkSignalForOpenShort(BigDecimal price, String figi, StrategyConfig config) {
        RSIStrategyConfig rsiconfig = (RSIStrategyConfig) config;
        // открываем шорт, если RSI > upper (70)
        if (!rsiconfig.checkForOpenShort(figi)) {
            return;
        }
        var shortOpen = ordersCache.shortOpen(figi);
        //Если уже куплен в шорт - выходим
        if (shortOpen) {
            return;
        }
        
        //закрываем лонг, если есть
        if (ordersCache.longOpen(figi)) {
            sellLong(price, figi);
        }
        
        buyShort(price, figi);
        
    }
    @Override
    public void checkSignalForCloseLong(BigDecimal price, String figi, StrategyConfig config) {
        RSIStrategyConfig rsiconfig = (RSIStrategyConfig) config;
        var longOpen = ordersCache.longOpen(figi);

        //Если уже продан в лонг, либо ничего нет - выходим
        if (!longOpen) {
            return;
        }

        if (!rsiconfig.checkForCloseLong(figi)) {
            return;
        }

        var takeProfit = rsiconfig.getTakeProfit().add(BigDecimal.ONE);
        var stopLoss = BigDecimal.ONE.subtract(rsiconfig.getStopLoss());

        var openPrice = ordersCache.getPrice(figi);
        var shortOpen = ordersCache.shortOpen(figi);
        String reason = null;
        if (shortOpen) {
            reason = "short opened";
        } else if (openPrice.multiply(takeProfit).compareTo(price) <= 0) {
            reason = "take profit";
        } else if (openPrice.multiply(stopLoss).compareTo(price) >= 0) {
            reason = "stop loss";
        }

        if (reason != null) {
            sellLong(price, figi);
        }
    }
    
    @Override
    public void checkSignalForCloseShort(BigDecimal price, String figi, StrategyConfig config) {
        RSIStrategyConfig rsiconfig = (RSIStrategyConfig) config;
        var shortOpen = ordersCache.shortOpen(figi);
        //Если уже продан в шорт - выходим
        if (!shortOpen) {
            return;
        }

        if (!rsiconfig.checkForCloseShort(figi)) {
            return;
        }

        var takeProfit = BigDecimal.ONE.subtract(rsiconfig.getTakeProfit());
        var stopLoss = rsiconfig.getStopLoss().add(BigDecimal.ONE);

        var openPrice = ordersCache.getPrice(figi);
        var longOpen = ordersCache.longOpen(figi);
        String reason = null;
        if (longOpen) {
            reason = "long opened";
        } else if (openPrice.multiply(takeProfit).compareTo(price) >= 0) {
            reason = "take profit";
        } else if (openPrice.multiply(stopLoss).compareTo(price) <= 0) {
            reason = "stop loss";
        }

        if (reason != null) {
            sellShort(price, figi);
        }
    }
       
    @Override
    public void buyLong(BigDecimal price, String figi) {
        orderService.buyMarketLong(figi);
    }
    
    @Override
    public void sellLong(BigDecimal price, String figi) {
        orderService.sellMarketLong(figi);
    }
    
    @Override
    public void buyShort(BigDecimal price, String figi) {
        orderService.buyMarketShort(figi);
    }
    
    @Override
    public void sellShort(BigDecimal price, String figi) {
        orderService.sellMarketShort(figi);
    }
}
