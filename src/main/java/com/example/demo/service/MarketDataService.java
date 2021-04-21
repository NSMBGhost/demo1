package com.example.demo.service;

import com.example.demo.bean.MarketDataType;

import java.util.Set;

public interface MarketDataService {
    void startServer();

    void stopServer();

    void processSubscribeAfterConnected();

    boolean subscribeMarketByType(MarketDataType marketDataType);

    boolean subscribeMarketByCodeSet(MarketDataType marketDataType, Set<String> codeSet);

    void replayMd(Set<String> codeSet);

    void queryAllMDConstant();

    void queryAllIndexConstant();
}
