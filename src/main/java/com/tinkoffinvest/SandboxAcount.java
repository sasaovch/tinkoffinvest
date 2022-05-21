package com.tinkoffinvest;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.util.StringUtils;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.PortfolioResponse;
import ru.tinkoff.piapi.core.SandboxService;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.math.BigDecimal;
import java.util.Objects;

// @Service
// @RequiredArgsConstructor
// @Slf4j
public class SandboxAcount {

    private ApiConnector sdkService;
    // @Value("${app.config.sandbox-account}")
    private String accountId;

    public String getAccountId() {
        if (Objects.nonNull(accountId) && accountId.length()>0) {
            // log.info("no sandbox account was set. creating a new one");
            SandboxService sandboxService = sdkService.getInvestApi().getSandboxService();
            accountId = sandboxService.openAccountSync();
            // log.info("new sandbox account: {}", accountId);
        }
        return accountId;
    }

    public PortfolioResponse getPortfolio() {
        return sdkService.getInvestApi().getSandboxService().getPortfolioSync(getAccountId());
    }

    public BigDecimal totalAmountOfFunds() {
        PortfolioResponse portfolio = getPortfolio();
        BigDecimal currencies = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountCurrencies());
        BigDecimal etfs = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountEtf());
        BigDecimal bonds = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountBonds());
        BigDecimal futures = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountFutures());
        BigDecimal shares = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountShares());
        BigDecimal total = currencies.add(etfs).add(bonds).add(futures).add(shares);
        // log.info("total: {}", total);
        return total;
    }

    private void addFunds(SandboxService sandboxService) {
        int amount = 1000000;
        String accountId = getAccountId();
        // log.info("add funds for sandbox account: {}. amount: {}", accountId, amount);
        sandboxService.payIn(accountId, MoneyValue.newBuilder().setCurrency("rub").setUnits(amount).build());
        sandboxService.payIn(accountId, MoneyValue.newBuilder().setCurrency("usd").setUnits(amount).build());
        // log.info("added funds for sandbox account: {}. amount: {}", accountId, amount);
    }
}