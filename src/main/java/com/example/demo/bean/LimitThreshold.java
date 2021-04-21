package com.example.demo.bean;

import java.math.BigDecimal;

public enum LimitThreshold {
    PERCENT_TEN(new BigDecimal(0.1)),
    PERCENT_TWENTY(new BigDecimal(0.2)),
    NO_LIMIT(new BigDecimal(1)),
    ;

    private BigDecimal value;

    private LimitThreshold(BigDecimal val) {
        this.value = val;
    }

    public BigDecimal getValue() {
        return value;
    }
}
