package com.tinkoffinvest.interfaceAPI;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter @Getter
@NoArgsConstructor
public class OrderInfo {
    private boolean shortOpen = false;
    private boolean longOpen = false;
    private int countLot;
    // price * lot;
    private BigDecimal priceOperation;
}
