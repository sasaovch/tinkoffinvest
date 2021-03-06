package com.tinkoffinvest.intervaltrading;

import java.util.Map;

import com.tinkoffinvest.baseclasses.ActiveOrdersMap;
import com.tinkoffinvest.baseclasses.OrderInfo;
import com.tinkoffinvest.baseclasses.StrategyConfig;
import com.tinkoffinvest.baseclasses.TypeOperation;
import com.tinkoffinvest.data.MyShare;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.utils.MapperUtils;

@Getter @Setter
@RequiredArgsConstructor
public class IntervalTradingStrategy implements StrategyConfig {
    @NonNull private ActiveOrdersMap activeOrdersMap;
    @NonNull private Map<String, MyShare> sharesInfo;

    @Override
    public boolean checkForOpenLong(String figi, Quotation priceQuotation) {
        float lowPrice = sharesInfo.get(figi).getLowPrice();
        float percentageGap = sharesInfo.get(figi).getPercentageGap();
        float minPriceOperation = Integer.MAX_VALUE;
        if (activeOrdersMap.get(figi) != null) {
            for (OrderInfo openOrder : activeOrdersMap.get(figi).values()) {
                float priceOrder = MapperUtils.quotationToBigDecimal(openOrder.getPriceOperation()).floatValue();
                if (priceOrder < minPriceOperation) {
                    minPriceOperation = priceOrder;
                }
            }
        }
        float checkPrice = MapperUtils.quotationToBigDecimal(priceQuotation).floatValue();
        float gap;
        if (minPriceOperation < checkPrice) {
            return false;
        } else if (checkPrice / minPriceOperation < percentageGap) {
            return false;
        }
        if (checkPrice >= lowPrice) {
            gap = (checkPrice / lowPrice - 1) * 100;
        } else {
            gap = (checkPrice / lowPrice) * 100;
        }
        if (gap <= percentageGap) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkForCloseLong(String figi, Quotation priceQuotation) {
        float percentageProfit = sharesInfo.get(figi).getPercentageProfit();
        float percentageLost = sharesInfo.get(figi).getPercentageLost();
        if (activeOrdersMap.get(figi) == null) {
            return false;
        }
        float checkPrice = MapperUtils.quotationToBigDecimal(priceQuotation).floatValue();
        for (OrderInfo openOrder : activeOrdersMap.get(figi).values()) {
            if (openOrder.getTypeOrder() == TypeOperation.LONG) {
                float priceOrder = MapperUtils.quotationToBigDecimal(openOrder.getPriceOperation()).floatValue();
                if (priceOrder / checkPrice - 1 >= percentageProfit) {
                    return true;
                }
                if (priceOrder / checkPrice >= percentageLost) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean checkForOpenShort(String figi, Quotation priceQuotation) {
        float highPrice = sharesInfo.get(figi).getHighPrice();
        float percentageGap = sharesInfo.get(figi).getPercentageGap();
        float maxPriceOperation = Integer.MIN_VALUE;
        if (activeOrdersMap.get(figi) != null) {
            for (OrderInfo openOrder : activeOrdersMap.get(figi).values()) {
                float priceOrder = MapperUtils.quotationToBigDecimal(openOrder.getPriceOperation()).floatValue();
                if (priceOrder > maxPriceOperation) {
                    maxPriceOperation = priceOrder;
                }
            }
        }
        float checkPrice = MapperUtils.quotationToBigDecimal(priceQuotation).floatValue();
        float gap;
        if (maxPriceOperation > checkPrice) {
            return false;
        } else if ((checkPrice / maxPriceOperation - 1) < percentageGap) {
            return false;
        }
        if (checkPrice >= highPrice) {
            gap = (checkPrice / highPrice - 1) * 100;
        } else {
            gap = (checkPrice / highPrice) * 100;
        }
        if (gap <= percentageGap) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkForCloseShort(String figi, Quotation priceQuotation) {
        float percentageProfit = sharesInfo.get(figi).getPercentageProfit();
        float percentageLost = sharesInfo.get(figi).getPercentageLost();
        if (activeOrdersMap.get(figi) == null) {
            return false;
        }
        float checkPrice = MapperUtils.quotationToBigDecimal(priceQuotation).floatValue();
        for (OrderInfo openOrder : activeOrdersMap.get(figi).values()) {
            if (openOrder.getTypeOrder() == TypeOperation.SHORT) {
                float priceOrder = MapperUtils.quotationToBigDecimal(openOrder.getPriceOperation()).floatValue();
                if (priceOrder / checkPrice - 1 >= percentageProfit) {
                    return true;
                }
                if (priceOrder / checkPrice >= percentageLost) {
                    return true;
                }
            }
        }
        return false;
    }
}
