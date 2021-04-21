package com.example.demo.service;

import com.example.demo.bean.ParamHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(1)
public class DataReceiveStarter implements ApplicationRunner {
    @Autowired
    private MarketDataService marketDataService;

    @Autowired
    private ParamHandler paramHandler;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if(!this.paramHandler.getStartup().isConnectInsight()) return;
        this.marketDataService.startServer();
    }

    //早上9点3分执行stock同步
    @Scheduled(cron = "0 3 9  * * * ")
    public void startStockSync() {
        if(!this.paramHandler.getStartup().isConnectInsight()) return;

        marketDataService.queryAllMDConstant();
    }

    //早上9点15分执行股票订阅
    @Scheduled(cron = "0 8 9 * * MON-FRI ")
    public void startSubscribeSync() {
        if(!this.paramHandler.getStartup().isConnectInsight()) return;

        this.marketDataService.processSubscribeAfterConnected();
    }

    //15点20分关闭server TODO 关闭动作需要跟华泰确认过
    // @Scheduled(cron = "0 20  15  * * * ")
    public void shutdownSubscribe() {
        this.marketDataService.stopServer();
    }
}
