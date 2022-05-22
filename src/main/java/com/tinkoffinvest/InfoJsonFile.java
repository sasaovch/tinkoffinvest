package com.tinkoffinvest;

import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter @Setter
@ToString
public class InfoJsonFile {
    public String token;
    public Set<MyShare> shares;
}
