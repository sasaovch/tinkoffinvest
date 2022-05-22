package com.tinkoffinvest.source;

import ru.tinkoff.piapi.contract.v1.Quotation;

public interface StrategyConfig {
    boolean checkForOpenLong(String figi, Quotation priceQuotation);
    boolean checkForCloseLong(String figi, Quotation priceQuotation);
    boolean checkForOpenShort(String figi, Quotation priceQuotation);
    boolean checkForCloseShort(String figi, Quotation priceQuotation);
}
