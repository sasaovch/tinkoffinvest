package com.tinkoffinvest.intervaltrading;

import lombok.extern.slf4j.Slf4j;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.util.UUID;

import com.tinkoffinvest.source.ActiveOrdersMap;
import com.tinkoffinvest.source.ApiConnector;
import com.tinkoffinvest.source.OrderInfo;
import com.tinkoffinvest.source.OrderService;
import com.tinkoffinvest.source.TypeOperation;

@Slf4j
public class OrderServiceImp implements OrderService{

    private ApiConnector apiConnector;
    private ActiveOrdersMap activeOrdersMap;
    private int numberOfLots;

    public PostOrderResponse sellMarket(String figi, Quotation priceQuotation, TypeOperation typeOrder) {
        OrderInfo orderInfo = new OrderInfo(figi, TypeOperation.LONG, priceQuotation);
        String orderId = UUID.randomUUID().toString();
        String accountId = apiConnector.getMainAccountId();
        PostOrderResponse orderResponse = apiConnector.postOrderSync(figi, numberOfLots, priceQuotation, OrderDirection.ORDER_DIRECTION_SELL, accountId, OrderType.ORDER_TYPE_MARKET, orderId); 
        orderInfo.setOrderId(orderId);
        if (typeOrder == TypeOperation.LONG){
            activeOrdersMap.removeMarketLong(figi, orderInfo);
            log.info("Sell Long: \norderId: " + orderId + "\npriceOperation: " + orderInfo.getPriceOperation());
        } else {
            activeOrdersMap.addMarketShort(figi, orderInfo);
            log.info("Buy Short: \norderId: " + orderId + "\npriceOperation: " + orderInfo.getPriceOperation());
        }
        return orderResponse;
    }

    public PostOrderResponse buyMarket(String figi, Quotation priceQuotation, TypeOperation typeOrder) {
        OrderInfo orderInfo = new OrderInfo(figi, TypeOperation.LONG, priceQuotation);
        String orderId = UUID.randomUUID().toString();
        String accountId = apiConnector.getMainAccountId();
        PostOrderResponse orderResponse = apiConnector.postOrderSync(figi, numberOfLots, priceQuotation, OrderDirection.ORDER_DIRECTION_BUY, accountId, OrderType.ORDER_TYPE_MARKET, orderId); 
        orderInfo.setOrderId(orderId);
        if (typeOrder == TypeOperation.LONG){
            activeOrdersMap.addMarketLong(figi, orderInfo);
            log.info("Buy Long: \norderId: " + orderId + "\npriceOperation: " + orderInfo.getPriceOperation());
        } else {
            activeOrdersMap.removeMarketShort(figi, orderInfo);
            log.info("Sell Short: \norderId: " + orderId + "\npriceOperation: " + orderInfo.getPriceOperation());
        }
        return orderResponse;
    }
}
