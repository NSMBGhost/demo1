package com.example.demo.bean;

public enum MarketDataType {
    UNKNOWN_DATA_TYPE(0, 0),
    MD_TICK(1, 1),
    MD_TRANSACTION(2, 2),
    MD_ORDER(3, 3),
    MD_CONSTANT(4, 4),
    MD_ETF_BASIC_INFO(6, 6),
    MD_KLINE_1MIN(7, 20),
    MD_KLINE_5MIN(8, 21),
    MD_KLINE_15MIN(9, 22),
    MD_KLINE_30MIN(10, 23),
    MD_KLINE_60MIN(11, 24),
    ;

    private int index;
    private int value;

    private MarketDataType(int index, int value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public int getValue() {
        return value;
    }
}
