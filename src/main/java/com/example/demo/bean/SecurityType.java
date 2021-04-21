package com.example.demo.bean;

public enum SecurityType {
    STOCK("StockType"),
    INDEX("IndexType"),
    ETF("Etf"),
    OPTIONS("OptionsType"),
    FUTURES("Futures"),
            ;

    private String insightValue;

    private SecurityType(String insightVal) {
        this.insightValue = insightVal;
    }

    public String getInsightValue() {
        return insightValue;
    }

    public static SecurityType parseFromInsight(String insightValue) {
        for(SecurityType object: SecurityType.values()) {
            if(object.insightValue.equals(insightValue)) {
                return object;
            }
        }
        return null;
    }
}
