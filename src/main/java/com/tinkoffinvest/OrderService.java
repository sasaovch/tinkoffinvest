package com.tinkoffinvest;

// import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.StopOrderDirection;
import ru.tinkoff.piapi.contract.v1.StopOrderType;
// import ru.tinkoff.rsistrategy.cache.StatusOrder;
import ru.tinkoff.piapi.core.InvestApi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

// @Service
@Slf4j
// @RequiredArgsConstructor
public class OrderService {

    private ApiConnector investApi;
    private StatusOrder ordersCache;
    private SandboxAcount sandboxAccountService;
    // @Value("${app.config.number-of-lots}")
    private int numberOfLots;


    public void sellMarketLong(String figi) {
        MoneyValue executedPrice = sellMarket(figi);
        ordersCache.setLong(figi, false);
        ordersCache.setPrice(figi, executedPrice);
    }

    public void buyMarketLong(String figi) {
        MoneyValue executedPrice = buyMarket(figi);
        ordersCache.setLong(figi, true);
        ordersCache.setPrice(figi, executedPrice);
    }

    public void sellMarketShort(String figi) {
        MoneyValue executedPrice = buyMarket(figi);
        ordersCache.setShort(figi, false);
        ordersCache.setPrice(figi, executedPrice);
    }

    public void buyMarketShort(String figi) {
        MoneyValue executedPrice = sellMarket(figi);
        ordersCache.setShort(figi, true);
        ordersCache.setPrice(figi, executedPrice);
    }

    private MoneyValue sellMarket(String figi) {
        String orderId = UUID.randomUUID().toString();
        String accountId = sandboxAccountService.getAccountId();
        return investApi.getInvestApi().getSandboxService().postOrderSync(figi, numberOfLots, Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_SELL, accountId, OrderType.ORDER_TYPE_MARKET, orderId).getTotalOrderAmount();
    }

    private MoneyValue buyMarket(String figi) {
        String orderId = UUID.randomUUID().toString();
        String accountId = sandboxAccountService.getAccountId();
        return investApi.getInvestApi().getSandboxService().postOrderSync(figi, numberOfLots, Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_BUY, accountId, OrderType.ORDER_TYPE_MARKET, orderId).getTotalOrderAmount();
    }

    private void stopOrdersServiceExample(InvestApi api, String figi) {
        //Выставляем стоп-заявку
        var accounts = api.getUserService().getAccountsSync();
        var mainAccount = accounts.get(0).getId();
    
        var lastPrice = api.getMarketDataService().getLastPricesSync(List.of(figi)).get(0).getPrice();
        var minPriceIncrement = api.getInstrumentsService().getInstrumentByFigiSync(figi).getMinPriceIncrement();
        var stopPrice = Quotation.newBuilder().setUnits(lastPrice.getUnits() - minPriceIncrement.getUnits() * 100)
          .setNano(lastPrice.getNano() - minPriceIncrement.getNano() * 100).build();
        var stopOrderId = api.getStopOrdersService()
          .postStopOrderGoodTillDateSync(figi, 1, stopPrice, stopPrice, StopOrderDirection.STOP_ORDER_DIRECTION_BUY,
            mainAccount, StopOrderType.STOP_ORDER_TYPE_STOP_LOSS, Instant.now().plus(1, ChronoUnit.DAYS));
        log.info("выставлена стоп-заявка. id: {}", stopOrderId);
    
        //Получаем список стоп-заявок и смотрим, что наша заявка в ней есть
        var stopOrders = api.getStopOrdersService().getStopOrdersSync(mainAccount);
        stopOrders.stream().filter(el -> el.getStopOrderId().equals(stopOrderId)).findAny().orElseThrow();
    
        // //Отменяем созданную стоп-заявку
        // api.getStopOrdersService().cancelStopOrder(mainAccount, stopOrderId);
        // log.info("стоп заявка с id {} отменена", stopOrderId);
      }

      private void ordersServiceExample(InvestApi api, String figi) {
        //Выставляем заявку
        var accounts = api.getUserService().getAccountsSync();
        var mainAccount = accounts.get(0).getId();
    
        var lastPrice = api.getMarketDataService().getLastPricesSync(List.of(figi)).get(0).getPrice();
        var minPriceIncrement = api.getInstrumentsService().getInstrumentByFigiSync(figi).getMinPriceIncrement();
        var price = Quotation.newBuilder().setUnits(lastPrice.getUnits() - minPriceIncrement.getUnits() * 100)
          .setNano(lastPrice.getNano() - minPriceIncrement.getNano() * 100).build();
    
        //Выставляем заявку на покупку по лимитной цене
        var orderId = api.getOrdersService()
          .postOrderSync(figi, 1, price, OrderDirection.ORDER_DIRECTION_BUY, mainAccount, OrderType.ORDER_TYPE_LIMIT,
            UUID.randomUUID().toString()).getOrderId();
    
        //Получаем список активных заявок, проверяем наличие нашей заявки в списке
        var orders = api.getOrdersService().getOrdersSync(mainAccount);
        if (orders.stream().anyMatch(el -> orderId.equals(el.getOrderId()))) {
          log.info("заявка с id {} есть в списке активных заявок", orderId);
        }
    
        // //Отменяем заявку
        // api.getOrdersService().cancelOrder(mainAccount, orderId);
      }
}