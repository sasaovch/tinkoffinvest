package com.tinkoffinvest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import ru.tinkoff.piapi.contract.v1.LastPrice;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.Share;

public class IntervalTradingStrategy implements Strategy {
    private ApiConnector apiConnection;
    private HashMap<String, Info> shares;
    private String codeStocks;

    public IntervalTradingStrategy(ApiConnector apiConnection) {
        this.apiConnection = apiConnection;
    }

    @Override
    public void start() {
        List<String> figiList = shares.entrySet().stream().map(s -> s.getValue().getShare().getFigi()).collect(Collectors.toList());
        List<LastPrice> lastPricesList = apiConnection.getInvestApi().getMarketDataService().getLastPricesSync(figiList);
        for (LastPrice lastPrice : lastPricesList) {
            var figi = lastPrice.getFigi();
            var price = quotationToBigDecimal(lastPrice.getPrice());
            analysePrice(figi, price);
        }
        // apiConnection.getInvestApi().getInstrumentsService().getShareByTicker(ticker, codeStocks);
    }

    private boolean analysePrice(String figi, BigDecimal price) {
        Info share = shares.get(figi);
        BigDecimal low = share.getLow();
        BigDecimal high = share.getHigh();;
        if (price.compareTo(low) < 0) {

        }
        return true;
    }

    public BigDecimal quotationToBigDecimal(Quotation value) {
        if (value == null) {
          return null;
        }
        return mapUnitsAndNanos(value.getUnits(), value.getNano());
    }

    public static BigDecimal mapUnitsAndNanos(long units, int nanos) {
    if (units == 0 && nanos == 0) {
        return BigDecimal.ZERO;
    }
    return BigDecimal.valueOf(units).add(BigDecimal.valueOf(nanos, 9));
    }

}