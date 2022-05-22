package com.tinkoffinvest.intervaltrading;

import java.math.BigDecimal;

import com.tinkoffinvest.source.ActiveOrdersMap;
import com.tinkoffinvest.source.OrderInfo;
import com.tinkoffinvest.source.StrategyConfig;
import com.tinkoffinvest.source.TypeOperation;

import ru.tinkoff.piapi.contract.v1.Quotation;

public class IntervalTradingStrategy implements StrategyConfig {
    private float highPrice, lowPrice;
    private float percentageGap;
    private float percentageProfit;
    private float percentageLost;
    private ActiveOrdersMap activeOrdersMap;

    @Override
    public boolean checkForOpenLong(String figi, Quotation priceQuotation) {
        float minPriceOperation = Integer.MAX_VALUE;
        if (activeOrdersMap.get(figi) != null) {
            for (OrderInfo openOrder : activeOrdersMap.get(figi).values()) {
                float priceOrder = quotationToBigDecimal(openOrder.getPriceOperation()).floatValue();
                if (priceOrder < minPriceOperation) {
                    minPriceOperation = priceOrder;
                }
            }
        }
        float checkPrice = quotationToBigDecimal(priceQuotation).floatValue();
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
        if (activeOrdersMap.get(figi) == null) {
            return false;
        }
        float checkPrice = quotationToBigDecimal(priceQuotation).floatValue();
        for (OrderInfo openOrder : activeOrdersMap.get(figi).values()) {
            if (openOrder.getTypeOrder() == TypeOperation.LONG) {
                float priceOrder = quotationToBigDecimal(openOrder.getPriceOperation()).floatValue();
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
        float maxPriceOperation = Integer.MIN_VALUE;
        if (activeOrdersMap.get(figi) != null) {
            for (OrderInfo openOrder : activeOrdersMap.get(figi).values()) {
                float priceOrder = quotationToBigDecimal(openOrder.getPriceOperation()).floatValue();
                if (priceOrder > maxPriceOperation) {
                    maxPriceOperation = priceOrder;
                }
            }
        }
        float checkPrice = quotationToBigDecimal(priceQuotation).floatValue();
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
        if (activeOrdersMap.get(figi) == null) {
            return false;
        }
        float checkPrice = quotationToBigDecimal(priceQuotation).floatValue();
        for (OrderInfo openOrder : activeOrdersMap.get(figi).values()) {
            if (openOrder.getTypeOrder() == TypeOperation.SHORT) {
                float priceOrder = quotationToBigDecimal(openOrder.getPriceOperation()).floatValue();
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
