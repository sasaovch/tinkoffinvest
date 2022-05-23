package com.tinkoffinvest.data;

import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter @Setter
@ToString
public class InfoJsonFile {
    public String exchange;
    public String token;
    public Boolean sandboxMode;
    public Float limitsMoney;
    public Set<MyShare> shares;
}
