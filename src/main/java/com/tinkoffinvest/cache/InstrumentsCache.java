package com.tinkoffinvest.cache;

import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.Instrument;

import java.math.BigDecimal;
import java.util.HashMap;
// import java.util.HashSet;
import java.util.Map;
// import java.util.Set;

import com.tinkoffinvest.service.SdkService;

// @Slf4j
@RequiredArgsConstructor
public class InstrumentsCache {

    private final Map<String, Instrument> instruments = new HashMap<>();

    private final SdkService sdkService;

    public void add(String figi) {
        var instrument = sdkService.getInvestApi().getInstrumentsService().getInstrumentByFigiSync(figi);
        instruments.put(figi, instrument);
    }

    public BigDecimal getLot(String figi) {
        if (!instruments.containsKey(figi)) {
            add(figi);
        }
        var lot = BigDecimal.valueOf(instruments.get(figi).getLot());
        if (lot.equals(BigDecimal.ZERO)) {
            throw new IllegalArgumentException("лот не может быть равен 0. figi: " + figi);
        }
        return lot;
    }
}
