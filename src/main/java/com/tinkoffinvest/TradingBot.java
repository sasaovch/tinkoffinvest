package com.tinkoffinvest;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.tinkoffinvest.baseclasses.AbstractTradingBot;
import com.tinkoffinvest.baseclasses.ApiConnector;
import com.tinkoffinvest.intervaltrading.IntervalTradingSignalHandler;
import com.tinkoffinvest.intervaltrading.IntervalTradingStrategy;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.tinkoff.piapi.contract.v1.LastPrice;
import ru.tinkoff.piapi.contract.v1.TradingDay;
import ru.tinkoff.piapi.core.utils.DateUtils;



@RequiredArgsConstructor
@Slf4j
public class TradingBot extends AbstractTradingBot {
    @NonNull private ApiConnector apiConnector;
    @NonNull private IntervalTradingSignalHandler signalHandler;
    @NonNull private IntervalTradingStrategy strategyConfig;
    @NonNull private String exchange;
    private boolean isWorking = true;
    private Scanner scanner = new Scanner(System.in);


    public void start() {
        while (isWorking) {
            log.info("TradingBot started");
            checkTime();
            List<LastPrice> lastPriceList = apiConnector.getMarketDataService().getLastPricesSync(strategyConfig.getSharesInfo().keySet());
            lastPriceList.stream().forEach(s -> {
                System.out.println(s.getFigi() + " " + s.getPrice());
                System.out.println(s);
                signalHandler.handle(s.getPrice(), strategyConfig, s.getFigi());
            });
            checkInput();
        }
    }

    public void checkInput() {
        try {
            isWorking = false;
            if (System.in.available() > 0) {
                String line = scanner.nextLine();
                if ("exit".equals(line)) {
                    log.info("TradingBot finished working.");
                    isWorking = false;
                }
                if ("print_porfolio".equals(line)) {
                    printPorfolio();
                }
            }
        } catch (IOException | NoSuchElementException e) {
            log.info("TradingBot finished working.");
            isWorking = false;
        }
    }

    public void checkTime() { 
        long now = System.currentTimeMillis();
        var tradingSchedules =
        apiConnector.getInstrumentsService().getTradingScheduleSync(exchange, Instant.now(), Instant.now());
        for (TradingDay tradingDay : tradingSchedules.getDaysList()) {
            var date = DateUtils.timestampToString(tradingDay.getDate());
            var startDate = DateUtils.timestampToString(tradingDay.getStartTime());
            var endDate = DateUtils.timestampToString(tradingDay.getEndTime());
            if (tradingDay.getIsTradingDay()) {
                log.info("Shedule of working {}. Date:{},  opem: {}, close: {}, current: {}", exchange, date, startDate, endDate, Instant.now());
                if ((tradingDay.getStartTime().getSeconds() - now / 1000 > 0) || (tradingDay.getEndTime().getSeconds() - now / 1000 < 0)) {
                    log.info("Exchange of {} is not working now", exchange);
                }
            } else {
            log.info("Today is non trading day");
            isWorking = false;
            }
        }
    }

    public void printPorfolio() {
        var portfolio = apiConnector.getInvestApi().getOperationsService().getPortfolioSync(
                apiConnector.getUserService().getAccountsSync().get(0).getId());
        var totalAmountShares = portfolio.getTotalAmountShares();
        log.info("Total value of shares in the portfolio {}", totalAmountShares);
        var positions = portfolio.getPositions();
        log.info("In protfolio {} positions", positions.size());
        for (int i = 0; i < Math.min(positions.size(), 5); i++) {
            var position = positions.get(i);
            var figi = position.getFigi();
            var quantity = position.getQuantity();
            var currentPrice = position.getCurrentPrice();
            var expectedYield = position.getExpectedYield();
            log.info(
                "Position by figi: {}, tool quantity: {}, current price of the instrument: {}, current calculated " +
                "profitability: {}",
                figi, quantity, currentPrice, expectedYield);
        }
    }
}
