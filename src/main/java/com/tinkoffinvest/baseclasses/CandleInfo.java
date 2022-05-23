package com.tinkoffinvest.baseclasses;

import java.math.BigDecimal;

import com.google.protobuf.Timestamp;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.utils.MapperUtils;

@EqualsAndHashCode(of = {"timestamp"})
@Getter
public class CandleInfo {

    private final BigDecimal open;
    private final BigDecimal close;
    private final BigDecimal low;
    private final BigDecimal high;
    private final Timestamp timestamp;

    private CandleInfo(Timestamp timestamp, Quotation openPrice, Quotation closePrice, Quotation lowPrice, Quotation highPrice) {
        this.open = MapperUtils.quotationToBigDecimal(openPrice);
        this.low = MapperUtils.quotationToBigDecimal(lowPrice);
        this.high = MapperUtils.quotationToBigDecimal(highPrice);
        this.close = MapperUtils.quotationToBigDecimal(closePrice);
        this.timestamp = timestamp;
    }

    public static CandleInfo ofHistoricCandle(HistoricCandle candle) {
        return new CandleInfo(candle.getTime(), candle.getOpen(), candle.getClose(), candle.getLow(), candle.getHigh());
    }

    public static CandleInfo ofStreamCandle(Candle candle) {
        return new CandleInfo(candle.getTime(), candle.getOpen(), candle.getClose(), candle.getLow(), candle.getHigh());
    }
}
