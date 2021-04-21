package com.example.demo.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TickEntity {
    @Field("Date")
    private Integer date;

    @Field("Time")
    private Integer time;

    @Field("Stock")
    private String stock;

    @Field("TradingPhaseCode")
    private String tradingPhaseCode;

    @Field("MaxPx")
    private BigDecimal maxPx;

    @Field("MinPx")
    private BigDecimal minPx;

    @Field("PreClosePx")
    private BigDecimal preClosePx;

    @Field("NumTrades")
    private Long numTrades;

    @Field("TotalVolumeTrade")
    private Long totalVolumeTrade;

    @Field("TotalValueTrade")
    private BigDecimal totalValueTrade;

    @Field("LastPx")
    private BigDecimal lastPx;

    @Field("OpenPx")
    private BigDecimal openPx;

    @Field("ClosePx")
    private BigDecimal closePx;

    @Field("HighPx")
    private BigDecimal highPx;

    @Field("LowPx")
    private BigDecimal lowPx;

    @Field("BuyPriceQueue")
    private List<BigDecimal> buyPriceQueue;

    @Field("BuyOrderQtyQueue")
    private List<Long> buyOrderQtyQueue;

    @Field("SellPriceQueue")
    private List<BigDecimal> sellPriceQueue;

    @Field("SellOrderQtyQueue")
    private List<Long> sellOrderQtyQueue;

    @Field("BuyNumOrdersQueue")
    private List<Long> buyNumOrdersQueue;

    @Field("SellNumOrdersQueue")
    private List<Long> sellNumOrdersQueue;

}
