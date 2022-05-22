package com.tinkoffinvest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tinkoffinvest.baseclasses.ActiveOrdersMap;
import com.tinkoffinvest.baseclasses.ApiConnector;
import com.tinkoffinvest.data.InfoJsonFile;
import com.tinkoffinvest.data.MyShare;
import com.tinkoffinvest.intervaltrading.IntervalTradingSignalHandler;
import com.tinkoffinvest.intervaltrading.IntervalTradingStrategy;
import com.tinkoffinvest.intervaltrading.OrderServiceImp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        // TODO Think about Stop-order
        // TODO Think about print portfolio
        // TODO Think about how to work with different instruments
        // TODO Think about list of candles of instrument
        try {
            Gson gson = new GsonBuilder().create();
            File file = new File("source.json");
            String strData = readfile(file);
            if (strData.equals("")) {
                LOGGER.error("File is empty.");
                return;
            }
            InfoJsonFile info = gson.fromJson(strData, InfoJsonFile.class);
            LOGGER.info("Create InfoJsonFile");
            ApiConnector apiConnector = new ApiConnector(info.getToken(), info.getSandboxMode());
            ActiveOrdersMap activeOrdersMap = new ActiveOrdersMap();
            OrderServiceImp orderService = new OrderServiceImp();
            LOGGER.info("ApiConnector");
            IntervalTradingSignalHandler signalHandler = new IntervalTradingSignalHandler(info.getLimitsMoney(), apiConnector, orderService);
            Map<String, MyShare> sharesInfo = new HashMap<>();
            for (MyShare share : info.getShares()) {
                sharesInfo.put(share.getFigi(), share);
            }
            IntervalTradingStrategy config = new IntervalTradingStrategy(activeOrdersMap, sharesInfo);
            TradingBot bot = new TradingBot(apiConnector, signalHandler, config);
            bot.start();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static String readfile(File file) throws FileNotFoundException, IOException {
        StringBuilder strData = new StringBuilder();
        String line;
        if (!file.exists()) {
            throw new FileNotFoundException();
        } else {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                while ((line = bufferedReader.readLine()) != null) {
                    strData.append(line);
                }
            }
        }
        return strData.toString();
    }
}
