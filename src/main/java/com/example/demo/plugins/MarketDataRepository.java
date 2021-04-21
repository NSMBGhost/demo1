package com.example.demo.plugins;

import com.example.demo.bean.EventType;
import com.example.demo.bean.MarketDataEvent;
import com.example.demo.bean.MessageType;
import com.example.demo.bean.StockMinute;
import com.example.demo.entity.TickEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Repository
public class MarketDataRepository {
    protected AtomicBoolean RUN_FLAGS = new AtomicBoolean(true);
    private LinkedBlockingDeque<MarketDataEvent> dataQueue = new LinkedBlockingDeque<>();

    @Autowired
    private TradeDayRepository tradeDayRepository;

    public void saveMinuteKLine(MessageType messageType, EventType eventType, StockMinute entity) {
        if(!this.tradeDayRepository.isTradeDay(entity.getDealDate())) {
            //如果数据的日期非交易日，直接过滤掉。防止测试数据入库
            return;
        }
        this.postDataEvent(new MarketDataEvent<StockMinute>(messageType, eventType, entity));
    }

    /**
     * 保存Tick数据，为了更精确的做数据检查，这里的Ticket数据是保留秒数的，真实存储到数据库中只保留分钟
     * @param entity
     */
    public void saveTickData(MessageType messageType, EventType eventType, TickEntity entity) {
        this.postDataEvent(new MarketDataEvent<TickEntity>(messageType, eventType, entity));
    }

    private void postDataEvent(MarketDataEvent dataEvent) {
        if(!RUN_FLAGS.get()) { return; }
        this.dataQueue.add(dataEvent);
    }

    public MarketDataEvent poolEvent() {
        return this.dataQueue.pollFirst();
    }

    @PreDestroy
    public void close() {
        RUN_FLAGS.set(false);
    }
}
