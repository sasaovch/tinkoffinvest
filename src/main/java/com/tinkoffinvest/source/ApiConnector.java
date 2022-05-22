package com.tinkoffinvest.source;

import java.util.UUID;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.InstrumentsService;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.MarketDataService;
import ru.tinkoff.piapi.core.OrdersService;
import ru.tinkoff.piapi.core.UsersService;

@Getter
@Slf4j
public class ApiConnector {
    private final InvestApi investApi;
    private final boolean sandboxMode;

    public ApiConnector(String token, boolean sandboxMode) {
        this.sandboxMode = sandboxMode;
        if (token == null || token.isBlank()){
            log.error("Empty token. Check environment variable 'token'");
            throw new IllegalArgumentException("Empty token. Check environment variable 'token'");
        }
        if (sandboxMode) {
            investApi = InvestApi.createSandbox(token);
            investApi.getSandboxService().openAccountSync();
            log.info("Created sandbox");
        } else {
            investApi = InvestApi.create(token);
            log.info("Created main account");
        }
    }

    public MarketDataService getMarketDataService() {
        return investApi.getMarketDataService();
    }

    public InstrumentsService getInstrumentsService() {
        return investApi.getInstrumentsService();
    }

    public OrdersService getOrdersService() {
        return investApi.getOrdersService();
    }

    public UsersService getUserService() {
        return investApi.getUserService();
    }

    public Account getMainAccount() {
        if (sandboxMode) {
            return investApi.getSandboxService().getAccountsSync().get(0);
        }
        return investApi.getUserService().getAccountsSync().get(0);
    }

    public String getMainAccountId() {
        if (sandboxMode) {
            return investApi.getSandboxService().getAccountsSync().get(0).getId();
        }
        return investApi.getUserService().getAccountsSync().get(0).getId();
    }

    public PostOrderResponse postOrderSync(String figi, long quantity, Quotation price, OrderDirection direction, String accountId, OrderType type, String orderId) {
        if (sandboxMode) {
            return investApi.getSandboxService().postOrderSync(figi, quantity, price, OrderDirection.ORDER_DIRECTION_BUY, accountId, type,
            UUID.randomUUID().toString());
        }
        return investApi.getOrdersService()
          .postOrderSync(figi, quantity, price, OrderDirection.ORDER_DIRECTION_BUY, accountId, type,
            UUID.randomUUID().toString());
    }
}
