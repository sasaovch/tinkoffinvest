package com.tinkoffinvest;

import ru.tinkoff.piapi.contract.v1.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.util.StringUtils;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.PortfolioResponse;
import ru.tinkoff.piapi.core.SandboxService;
import ru.tinkoff.piapi.core.UsersService;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class MyAccount {
    private final ApiConnector investApi;    
    private String accountId;
    private List<Account> account;
    private final boolean sandboxMode;

    public List<Account> getAccountId() {
        // if (!StringUtils.hasLength(accountId)) {
        //     log.info("no sandbox account was set. creating a new one");
            UsersService userService = investApi.getInvestApi().getUserService();
            account = userService.getAccountsSync();
            // log.info("new sandbox account: {}", accountId);
        //}
        return account;
    }

    // public PortfolioResponse getPortfolio() {
    //     investApi.getInvestApi().getUserService().getInfoSync();
    //     return investApi.getInvestApi().getSandboxService().getPortfolioSync(getAccountId());
    // }

    // public BigDecimal totalAmountOfFunds() {
    //     var portfolio = getPortfolio();
    //     var currencies = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountCurrencies());
    //     var etfs = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountEtf());
    //     var bonds = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountBonds());
    //     var futures = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountFutures());
    //     var shares = MapperUtils.moneyValueToBigDecimal(portfolio.getTotalAmountShares());
    //     var total = currencies.add(etfs).add(bonds).add(futures).add(shares);
    //     //log.info("total: {}", total);
    //     return total;
    // }

    private void addFunds(UsersService usersService) {
        var amount = 1000000;
        var accountId = getAccountId();
        //log.info("add funds for sandbox account: {}. amount: {}", accountId, amount);
        // usersService.payIn(accountId, MoneyValue.newBuilder().setCurrency("rub").setUnits(amount).build());
        // usersService.payIn(accountId, MoneyValue.newBuilder().setCurrency("usd").setUnits(amount).build());
        //log.info("added funds for sandbox account: {}. amount: {}", accountId, amount);
    }
}
