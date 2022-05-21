package com.tinkoffinvest;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.tinkoff.piapi.contract.v1.Share;

@Getter @RequiredArgsConstructor
public class Info {
    private BigDecimal low;
    private BigDecimal high;
    private int count;
    private Share share;
    private float percentage;
}
