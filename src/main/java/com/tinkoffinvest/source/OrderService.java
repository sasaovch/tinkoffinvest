package com.tinkoffinvest.source;

import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.tinkoff.piapi.contract.v1.Quotation;

public interface OrderService {
    PostOrderResponse buyMarket(String figi, Quotation priceQuotation, TypeOperation typeOrder);
    PostOrderResponse sellMarket(String figi, Quotation priceQuotation, TypeOperation typeOrder);  
}
