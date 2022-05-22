package com.tinkoffinvest.baseclasses;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.tinkoff.piapi.contract.v1.Quotation;

@Setter @Getter
@RequiredArgsConstructor
public class OrderInfo {
    @NonNull private String figi;
    @NonNull private TypeOperation typeOrder;
    @NonNull private Quotation priceOperation; // price * lot
    private int countLot;
    private String orderId;
}
