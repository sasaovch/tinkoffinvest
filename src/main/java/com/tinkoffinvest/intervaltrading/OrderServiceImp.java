package com.tinkoffinvest.intervaltrading;

import lombok.Getter;
import lombok.Setter;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.util.UUID;

import com.tinkoffinvest.baseclasses.ActiveOrdersMap;
import com.tinkoffinvest.baseclasses.ApiConnector;
import com.tinkoffinvest.baseclasses.OrderInfo;
import com.tinkoffinvest.baseclasses.OrderService;
import com.tinkoffinvest.baseclasses.TypeOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter @Setter
public class OrderServiceImp implements OrderService{
    private ApiConnector apiConnector;
    private ActiveOrdersMap activeOrdersMap;
    private int numberOfLots;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImp.class);

    public PostOrderResponse sellMarket(String figi, Quotation priceQuotation, TypeOperation typeOrder) {
        OrderInfo orderInfo = new OrderInfo(figi, TypeOperation.LONG, priceQuotation);
        String orderId = UUID.randomUUID().toString();
        String accountId = apiConnector.getMainAccountId();
        PostOrderResponse orderResponse = apiConnector.postOrderSync(figi, numberOfLots, priceQuotation, OrderDirection.ORDER_DIRECTION_SELL, accountId, OrderType.ORDER_TYPE_MARKET, orderId); 
        orderInfo.setOrderId(orderId);
        if (typeOrder == TypeOperation.LONG){
            activeOrdersMap.removeMarketLong(figi, orderInfo);
            LOGGER.info("Sell Long: \norderId: " + orderId + "\npriceOperation: " + orderInfo.getPriceOperation());
        } else {
            activeOrdersMap.addMarketShort(figi, orderInfo);
            LOGGER.info("Buy Short: \norderId: " + orderId + "\npriceOperation: " + orderInfo.getPriceOperation());
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
            LOGGER.info("Buy Long: \norderId: " + orderId + "\npriceOperation: " + orderInfo.getPriceOperation());
        } else {
            activeOrdersMap.removeMarketShort(figi, orderInfo);
            LOGGER.info("Sell Short: \norderId: " + orderId + "\npriceOperation: " + orderInfo.getPriceOperation());
        }
        return orderResponse;
    }
}
