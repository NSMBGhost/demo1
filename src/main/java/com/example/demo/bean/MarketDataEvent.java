package com.example.demo.bean;

import lombok.Data;

import java.awt.*;
import java.util.Map;

@Data
public class MarketDataEvent<T> {
    private MessageType messageType;
    private EventType eventType;
    private T data;
    private Map<String, Object> opts;

    public MarketDataEvent(MessageType messageType, EventType eventType, T data) {
        this.messageType = messageType;
        this.eventType = eventType;
        this.data = data;
    }

}
