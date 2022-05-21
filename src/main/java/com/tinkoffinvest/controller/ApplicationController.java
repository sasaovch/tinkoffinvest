package com.tinkoffinvest.controller;

import lombok.RequiredArgsConstructor;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

import com.tinkoffinvest.cache.CandlesCache;
import com.tinkoffinvest.model.RSIStrategyConfig;
import com.tinkoffinvest.service.SandboxAccountService;

@RequiredArgsConstructor
public class ApplicationController {

    private final CandlesCache candlesCache;
    private final SandboxAccountService sandboxAccountService;


    // @PostMapping("/rsi")
    public List<RSIStrategyConfig> start(List<RSIStrategyConfig> configs) {
        candlesCache.initCache(configs);
        return configs;
    }

    // @GetMapping("/portfolio")
    public BigDecimal getPortfolio() {
        return sandboxAccountService.totalAmountOfFunds();
    }

}
