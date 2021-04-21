package com.example.demo.bean;

public enum EventType {
    EX_RIGHTS_FACTOR(3, "复权因子", false),
    BALANCE(4,"负债数据",false),
    CASH_FLOW(5,"现金流数据",false),
    INDICATOR(6,"财务指标数据",false),
    INCOME(7,"收益数据",false),
    VALUATION(8, "估值日数据", false),
    INDEX_STOCK_POOL(9, "指数股票池", true),
    FUTURE_DAILY(11, "期货日数据", false),
    FUTURE_MINUTE(12, "期货分钟数据", true),
    FUTURE_TICK(13, "期货TICK", true),
    INDEX_DAILY(21, "指数日数据", false),
    INDEX_MINUTE(22, "指数分钟数据", true),
    INDEX_TICK(23, "指数TICK", true),
    STOCK_DAILY(31, "股票日数据", false),
    STOCK_MINUTE(32, "股票分钟数据", true),
    STOCK_TICK(33, "股票TICK", true),

    ETF_DAILY(51, "ETF日数据", true),
    ETF_MINUTE(52, "ETF分钟数据", true),
    ETF_TICK(53, "ETF TICK", true),
    ;

    private boolean fetchByDay;
    private int value;
    private String label;

    private EventType(int val, String text, boolean fetchByDay) {
        this.value = val;
        this.label = text;
        this.fetchByDay = fetchByDay;
    }

    public static EventType valueOf(int value) {
        for(EventType e: EventType.values()) {
            if(e.getValue() == value) {
                return e;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
    public String getLabel() {
        return label;
    }

    public boolean isFetchByDay() {
        return fetchByDay;
    }
}
