package com.tinkoffinvest.baseclasses;

import java.math.BigDecimal;

import com.google.protobuf.Timestamp;

import lombok.Getter;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.utils.MapperUtils;

@Getter
public class CandleInfo {
    private final BigDecimal openPrice;
    private final BigDecimal closePrice;
    private final Timestamp timestamp;

    private CandleInfo(Quotation closePrice, Quotation openPrice, Timestamp timestamp, BigDecimal lot) {
        this.openPrice = MapperUtils.quotationToBigDecimal(openPrice).multiply(lot);
        this.closePrice = MapperUtils.quotationToBigDecimal(closePrice).multiply(lot);
        this.timestamp = timestamp;
    }

    public static CandleInfo ofHistoricCandle(HistoricCandle candle, BigDecimal lot) {
        return new CandleInfo(candle.getClose(), candle.getOpen(), candle.getTime(), lot);
    }

    public static CandleInfo ofStreamCandle(Candle candle, BigDecimal lot) {
        return new CandleInfo(candle.getClose(), candle.getOpen(), candle.getTime(), lot);
    }
}
