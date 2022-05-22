package com.tinkoffinvest.baseclasses;

import ru.tinkoff.piapi.contract.v1.Quotation;

public interface SignalHandler {
    void handle(Quotation priceQuotation, StrategyConfig config, String figi);
    void checkSignalForOpenLong(Quotation priceQuotation, String figi, StrategyConfig config);
    void checkSignalForOpenShort(Quotation priceQuotation, String figi, StrategyConfig config);
    void checkSignalForCloseLong(Quotation priceQuotation, String figi, StrategyConfig config);
    void checkSignalForCloseShort(Quotation priceQuotation, String figi, StrategyConfig config);
    void openLong(Quotation priceQuotation, String figi);
    void closeLong(Quotation priceQuotation, String figi);
    void openShort(Quotation priceQuotation, String figi);
    void closeShort(Quotation priceQuotation, String figi);
}
