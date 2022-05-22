package com.tinkoffinvest.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter @Setter
@ToString
public class MyShare {
    public String figi;
    public float lowPrice;
    public float highPrice;
    public float percentageGap;
    public float percentageProfit;
    public float percentageLost;
    public int lotOperationSize;
}
