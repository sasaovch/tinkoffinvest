package com.tinkoffinvest.intervaltrading;

import java.math.BigDecimal;

import com.tinkoffinvest.baseclasses.ApiConnector;
import com.tinkoffinvest.baseclasses.SignalHandler;
import com.tinkoffinvest.baseclasses.StrategyConfig;
import com.tinkoffinvest.baseclasses.TypeOperation;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.Share;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter @Setter
@RequiredArgsConstructor
public class IntervalTradingSignalHandler implements SignalHandler {
    @NonNull private Float limitsMoney;
    @NonNull private ApiConnector apiConnector;
    @NonNull private OrderServiceImp orderService;
    private Float cashBalance = 0F;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImp.class);


    @Override
    public void handle(Quotation priceQuotation, StrategyConfig config, String figi) {
        LOGGER.info("In handle");
        checkSignalForOpenShort(priceQuotation, figi, config);
        checkSignalForOpenLong(priceQuotation, figi, config);
        checkSignalForCloseShort(priceQuotation, figi, config);
        checkSignalForCloseLong(priceQuotation, figi, config);
    }

    @Override
    public void checkSignalForOpenLong(Quotation priceQuotation, String figi, StrategyConfig config) {
        LOGGER.info("In checkSignalForOpenLong");
        IntervalTradingStrategy intTradconfig = (IntervalTradingStrategy) config;
        BigDecimal price = quotationToBigDecimal(priceQuotation);

        if (!intTradconfig.checkForOpenLong(figi, priceQuotation)) {
            return;
        }

        Share share = apiConnector.getInstrumentsService().getShareByFigiSync(figi);
        int lotSize = share.getLot();
        float lotPrice = lotSize * price.floatValue() * orderService.getNumberOfLots();

        if (limitsMoney - cashBalance - lotPrice < 0) {
            return;
        }
        openLong(priceQuotation, figi);
        cashBalance += lotPrice;
    }

    @Override
    public void checkSignalForOpenShort(Quotation priceQuotation, String figi, StrategyConfig config) {
        LOGGER.info("In checkSignalForOpenShort");
        IntervalTradingStrategy intTradconfig = (IntervalTradingStrategy) config;
        // открываем шорт, если RSI > upper (70)
        if (!intTradconfig.checkForOpenShort(figi, priceQuotation)) {
            return;
        }
        Share share = apiConnector.getInstrumentsService().getShareByFigiSync(figi);
        int lotSize = share.getLot();
        BigDecimal price = quotationToBigDecimal(priceQuotation);
        float lotPrice = lotSize * price.floatValue() * orderService.getNumberOfLots();
        if (limitsMoney - cashBalance - lotPrice < 0) {
            return;
        }
        openShort(priceQuotation, figi);
        cashBalance += lotPrice;
    }

    @Override
    public void checkSignalForCloseLong(Quotation priceQuotation, String figi, StrategyConfig config) {
        LOGGER.info("In checkSignalForCloseLong");
        IntervalTradingStrategy intTradconfig = (IntervalTradingStrategy) config;
        if (!intTradconfig.checkForCloseLong(figi, priceQuotation)) {
            return;
        }
        Share share = apiConnector.getInstrumentsService().getShareByFigiSync(figi);
        int lotSize = share.getLot();
        BigDecimal price = quotationToBigDecimal(priceQuotation);
        float lotPrice = lotSize * price.floatValue() * orderService.getNumberOfLots();
        closeLong(priceQuotation, figi);
        cashBalance -= lotPrice;
    }

    @Override
    public void checkSignalForCloseShort(Quotation priceQuotation, String figi, StrategyConfig config) {
        LOGGER.info("In checkSignalForCloseShort");
        IntervalTradingStrategy intTradconfig = (IntervalTradingStrategy) config;
        if (!intTradconfig.checkForCloseLong(figi, priceQuotation)) {
            return;
        }
        Share share = apiConnector.getInstrumentsService().getShareByFigiSync(figi);
        int lotSize = share.getLot();
        BigDecimal price = quotationToBigDecimal(priceQuotation);
        float lotPrice = lotSize * price.floatValue() * orderService.getNumberOfLots();
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
