package com.tinkoffinvest.baseclasses;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class ActiveOrdersMap {

    private Map<String, Map<String, OrderInfo>> activeOrdersMap = new HashMap<>();
    
    public Map<String, OrderInfo> get(String figi) {
        return activeOrdersMap.get(figi);
    }

    public void addMarketLong(String figi, OrderInfo info) {
        if (activeOrdersMap.containsKey(figi)) {
            activeOrdersMap.get(figi).put(info.getOrderId(), info);
        }
        Map<String, OrderInfo> newHashSet = new HashMap<>();
        newHashSet.put(info.getOrderId(), info);
        activeOrdersMap.put(figi, newHashSet);
    }

    public void removeMarketLong(String figi, OrderInfo info) {
        activeOrdersMap.get(figi).remove(info.getOrderId());
    }

    public void addMarketShort(String figi, OrderInfo info) {
        if (activeOrdersMap.containsKey(figi)) {
            activeOrdersMap.get(figi).put(info.getOrderId(), info);
        }
        Map<String, OrderInfo> newHashSet = new HashMap<>();
        newHashSet.put(info.getOrderId(), info);
        activeOrdersMap.put(figi, newHashSet);
    }

    public void removeMarketShort(String figi, OrderInfo info) {
        activeOrdersMap.get(figi).remove(info.getOrderId());
    }
}
