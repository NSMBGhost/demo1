package com.example.demo.common;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class Money implements Serializable {
    private static final long serialVersionUID = -1632355669613064468L;

    public static BigDecimal displayValue(Long realValue, int scale){
        if(Objects.isNull(realValue)) {
            return null;
        }
        return new BigDecimal(realValue).divide(new BigDecimal(Math.pow(10, scale)));
    }

    public static BigDecimal addOneCent(BigDecimal value) {
        return value.add(new BigDecimal("0.01"));
    }

    public static BigDecimal subtractOneCent(BigDecimal value) {
        return value.subtract(new BigDecimal("0.01"));
    }

    public static long realValue(BigDecimal displayValue, int scale){
        return displayValue.multiply(new BigDecimal(Math.pow(10, scale))).longValue();
    }

    public static BigDecimal parseFromString(String strValue) {
        if(StringUtils.isBlank(strValue)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(strValue);
    }

}
