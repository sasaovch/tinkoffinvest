package com.tinkoffinvest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.tinkoff.piapi.contract.v1.Asset;
import ru.tinkoff.piapi.contract.v1.Dividend;
import ru.tinkoff.piapi.contract.v1.LastPrice;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.contract.v1.TradingDay;
import ru.tinkoff.piapi.core.InstrumentsService;
import static ru.tinkoff.piapi.core.utils.DateUtils.timestampToString;
import static ru.tinkoff.piapi.core.utils.MapperUtils.quotationToBigDecimal;

@Slf4j
@Getter
public class ContextProvider {

    private ApiConnector apiConnector;
    private InstrumentsService instrumentsService;

    public ContextProvider(ApiConnector apiConnector) {
        this.apiConnector = apiConnector;
        instrumentsService = apiConnector.getInvestApi().getInstrumentsService();
    }

    public Share getShareBy(String figi) throws Exception {
        return instrumentsService.getShareByFigiSync(figi);
    }

    public void getTradableInstruments() {
        var shares = instrumentsService.getTradableSharesSync();
        for (int i = 0; i < Math.min(shares.size(), 3); i++) {
            var share = shares.get(i);
            var figi = share.getFigi();
            var dividends =
              instrumentsService.getDividendsSync(figi, Instant.now(), Instant.now().plus(30, ChronoUnit.DAYS));
              int lot = share.getLot();
              MoneyValue nominal = share.getNominal();
            for (Dividend dividend : dividends) {
              log.info("Nominal {},{} lot {}, figi {}", nominal.getUnits(), nominal.getNano(), lot, figi);
            }
          }
      
    }

    public void getTradingSchedule() {
        //Получаем время работы биржи
        var tradingSchedules =
            instrumentsService.getTradingScheduleSync("spb", Instant.now(), Instant.now().plus(5, ChronoUnit.DAYS));
        for (TradingDay tradingDay : tradingSchedules.getDaysList()) {
        var date = timestampToString(tradingDay.getDate());
        var startDate = timestampToString(tradingDay.getStartTime());
        var endDate = timestampToString(tradingDay.getEndTime());
        if (tradingDay.getIsTradingDay()) {
            log.info("расписание торгов для площадки SPB. Дата: {},  открытие: {}, закрытие: {}", date, startDate, endDate);
        } else {
            log.info("расписание торгов для площадки SPB. Дата: {}. Выходной день", date);
        }
        }
    }

    public void getListActives() {
        var assets = instrumentsService.getAssetsSync();
        for (Asset asset : assets) {
        log.info("актив. uid : {}, имя: {}, тип: {}", asset.getUid(), asset.getName(), asset.getType());
        }
    }

    public void getLastPrice(List<String> figiList) {
        var lastPrices = apiConnector.getInvestApi().getMarketDataService().getLastPricesSync(figiList);
        for (LastPrice lastPrice : lastPrices) {
        var figi = lastPrice.getFigi();
        var price = quotationToBigDecimal(lastPrice.getPrice());
        var time = timestampToString(lastPrice.getTime());
        log.info("последняя цена по инструменту {}, цена: {}, время обновления цены: {}", figi, price, time);
        }
    }

    // public MarketInstrumentList getBonds() throws Exception {
    //     return getOpenApi().getMarketContext().getMarketBonds().join();
    // }

    // public MarketInstrumentList getEtfs() throws Exception {
    //     return getOpenApi().getMarketContext().getMarketEtfs().join();
    // }

    // public MarketInstrumentList getCurrencies() throws Exception {
    //     return getOpenApi().getMarketContext().getMarketCurrencies().join();
    // }
}