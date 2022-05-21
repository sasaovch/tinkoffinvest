package com.tinkoffinvest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tinkoffinvest.interfaceAPI.StrategyConfig;

import lombok.Data;
// import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

// settings for strategy

@Data
// @Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class RSIStrategyConfig implements StrategyConfig{

    private List<String> figi;
    private BigDecimal upperRsiThreshold = BigDecimal.valueOf(70);
    private BigDecimal lowerRsiThreshold = BigDecimal.valueOf(30);
    private BigDecimal takeProfit = BigDecimal.valueOf(0.15);
    private BigDecimal stopLoss = BigDecimal.valueOf(0.05);
    private int rsiPeriod = 14;
    @Override
    public boolean checkForOpenLong(String figi) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean checkForOpenShort(String figi) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean checkForCloseLong(String figi) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean checkForCloseShort(String figi) {
        // TODO Auto-generated method stub
        return false;
    }
}
