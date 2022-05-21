package com.tinkoffinvest.interfaceAPI;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.tinkoff.piapi.core.InvestApi;

@Getter
@Slf4j
public class ApiConnector {
    private final InvestApi investApi;
    private final boolean sandboxMode;

    public ApiConnector(String token, boolean sandboxMode) {
        this.sandboxMode = sandboxMode;
        if (token == null || token.isBlank()){
            log.error("Empty token. Check environment variable 'token'");
            throw new IllegalArgumentException("Empty token. Check environment variable 'token'");
        }
        if (sandboxMode) {
            investApi = InvestApi.createSandbox(token);
            log.info("Created sandbox");
        } else {
            investApi = InvestApi.create(token);
            log.info("Created main account");
        }
    }
}
