package com.tinkoffinvest.intervaltrading;

import java.math.BigDecimal;

import com.tinkoffinvest.source.ApiConnector;
import com.tinkoffinvest.source.OrderService;
import com.tinkoffinvest.source.SignalHandler;
import com.tinkoffinvest.source.StrategyConfig;
import com.tinkoffinvest.source.TypeOperation;

import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.Share;

public class IntervalTradingSignalHandler implements SignalHandler {
    private float limitsMoney;
    private float cashBalance;
    private int lotOperation;
    private ApiConnector apiConnector;
    private OrderService orderService;

    @Override
    public void handle(Quotation priceQuotation, StrategyConfig config, String figi) {
        checkSignalForOpenShort(priceQuotation, figi, config);
        checkSignalForOpenLong(priceQuotation, figi, config);
        checkSignalForCloseShort(priceQuotation, figi, config);
        checkSignalForCloseLong(priceQuotation, figi, config);
    }

    @Override
    public void checkSignalForOpenLong(Quotation priceQuotation, String figi, StrategyConfig config) {
        IntervalTradingStrategy intTradconfig = (IntervalTradingStrategy) config;
        BigDecimal price = quotationToBigDecimal(priceQuotation);

        if (!intTradconfig.checkForOpenLong(figi, priceQuotation)) {
            return;
        }

        Share share = apiConnector.getInstrumentsService().getShareByFigiSync(figi);
        int lotSize = share.getLot();
        float lotPrice = lotSize * price.floatValue() * lotOperation;

        if (limitsMoney - cashBalance - lotPrice < 0) {
            return;
        }
        openLong(priceQuotation, figi);
        cashBalance += lotPrice;
    }

    @Override
    public void checkSignalForOpenShort(Quotation priceQuotation, String figi, StrategyConfig config) {
        IntervalTradingStrategy intTradconfig = (IntervalTradingStrategy) config;
        // открываем шорт, если RSI > upper (70)
        if (!intTradconfig.checkForOpenShort(figi, priceQuotation)) {
            return;
        }
        Share share = apiConnector.getInstrumentsService().getShareByFigiSync(figi);
        int lotSize = share.getLot();
        BigDecimal price = quotationToBigDecimal(priceQuotation);
        float lotPrice = lotSize * price.floatValue() * lotOperation;
        if (limitsMoney - cashBalance - lotPrice < 0) {
            return;
        }
        openShort(priceQuotation, figi);
        cashBalance += lotPrice;
    }

    @Override
    public void checkSignalForCloseLong(Quotation priceQuotation, String figi, StrategyConfig config) {
        IntervalTradingStrategy intTradconfig = (IntervalTradingStrategy) config;
        if (!intTradconfig.checkForCloseLong(figi, priceQuotation)) {
            return;
        }
        Share share = apiConnector.getInstrumentsService().getShareByFigiSync(figi);
        int lotSize = share.getLot();
        BigDecimal price = quotationToBigDecimal(priceQuotation);
        float lotPrice = lotSize * price.floatValue() * lotOperation;
        closeLong(priceQuotation, figi);
        cashBalance -= lotPrice;
    }

    @Override
    public void checkSignalForCloseShort(Quotation priceQuotation, String figi, StrategyConfig config) {
        IntervalTradingStrategy intTradconfig = (IntervalTradingStrategy) config;
        if (!intTradconfig.checkForCloseLong(figi, priceQuotation)) {
            return;
        }
        Share share = apiConnector.getInstrumentsService().getShareByFigiSync(figi);
        int lotSize = share.getLot();
        BigDecimal price = quotationToBigDecimal(priceQuotation);
        float lotPrice = lotSize * price.floatValue() * lotOperation;
        closeShort(priceQuotation, figi);
        cashBalance -= lotPrice;
    }

    @Override
    public void openLong(Quotation priceQuotation, String figi) {
        orderService.buyMarket(figi, priceQuotation, TypeOperation.LONG);
    }

    @Override
    public void closeLong(Quotation priceQuotation, String figi) {
        orderService.sellMarket(figi, priceQuotation, TypeOperation.LONG);
    }

    @Override
    public void openShort(Quotation priceQuotation, String figi) {
        orderService.sellMarket(figi, priceQuotation, TypeOperation.SHORT);
    }

    @Override
    public void closeShort(Quotation priceQuotation, String figi) {
        orderService.buyMarket(figi, priceQuotation, TypeOperation.SHORT);
    }

    public static BigDecimal quotationToBigDecimal(Quotation value) {
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
