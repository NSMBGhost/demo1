package com.example.demo.service.impl;

import com.example.demo.bean.*;
import com.example.demo.common.Money;
import com.example.demo.entity.TickEntity;
import com.example.demo.plugins.MarketDataRepository;
import com.example.demo.service.SecurityInfoService;

import com.example.demo.utils.BizUtils;
import com.example.demo.utils.DateUtils;
import com.htsc.mdc.insight.model.MarketDataProto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class MarketDataDispatcher {
    private static final String[] SECURITY_SUB_TYPES = new String[]{ "02200", "02001", "02002", "02003"};

    @Autowired
    private ParamHandler paramHandler;

    @Autowired
    private MarketDataRepository marketDataRepository;

    @Autowired
    private SecurityInfoService securityInfoService;

    public void onReceiveMarketData(MessageType messageType, long interactionId, MarketDataProto.MarketData marketData) {
        if (marketData.hasMdSimpleTick()) {
            Map<String, Object> message = ProtobufFormatter.formatMessage(marketData.getMdSimpleTick());
            this.marketDataRepository.saveTickData(messageType, EventType.STOCK_TICK, this.transToTickEntity(message));
        } else if (marketData.hasMdStock()) {
            Map<String, Object> message = ProtobufFormatter.formatMessage(marketData.getMdStock());
            // 股票行情数据
            log.debug(messageType + ":MdStock interactionId=" + interactionId + ",MdStock="
                    + ProtobufFormatter.formatMessage(marketData.getMdStock()));
            this.marketDataRepository.saveTickData(messageType, EventType.STOCK_TICK, this.transToTickEntity(message));
        } else if (marketData.hasMdIndex()) {
            Map<String, Object> message = ProtobufFormatter.formatMessage(marketData.getMdIndex());

            // 指数行情数据
            if (this.paramHandler.getDebug().isMarket())
                log.debug(messageType + ":MdIndex interactionId=" + interactionId + ",MdIndex="
                        + ProtobufFormatter.formatMessage(marketData.getMdIndex()));
            this.marketDataRepository.saveTickData(messageType, EventType.INDEX_TICK, this.transToIndexTickEntity(message));
        } else if (marketData.hasMdBond()) {
            // 债券行情数据
            if (this.paramHandler.getDebug().isMarket())
                log.info(messageType + ":MdBond interactionId=" + interactionId + ",MdBond="
                        + ProtobufFormatter.formatMessage(marketData.getMdBond()));
        } else if (marketData.hasMdFund()) {
            // 基金行情数据
            if (this.paramHandler.getDebug().isMarket())
                log.info(messageType + ":MdFund interactionId=" + interactionId + ",MdFund="
                        + ProtobufFormatter.formatMessage(marketData.getMdFund()));
        } else if (marketData.hasMdFuture()) {
            // 期货行情数据
            if (this.paramHandler.getDebug().isMarket())
                log.info(messageType + ":MdFuture interactionId=" + interactionId + ",MdFuture="
                        + ProtobufFormatter.formatMessage(marketData.getMdFuture()));
        } else if (marketData.hasMdConstant()) {
            // 证券基础信息
            Map<String, Object> message = ProtobufFormatter.formatMessage(marketData.getMdConstant());
            if (message.get("securityType").equals("StockType") &&
                    BizUtils.hasElem(SECURITY_SUB_TYPES, message.get("SecuritySubType"))) {
                SecurityInfo info = new SecurityInfo();
                info.setSecurityId((String) message.get("HTSCSecurityID"));
                info.setDisplayName((String) message.get("Symbol"));
                info.setSimpleName((String) message.get("ChiSpelling"));
                info.setStartDate((String) message.get("ListDate"));
                info.setEndDate("2099-12-31");
                String securityType = (String)message.get("securityType");
                info.setSecurityType(SecurityType.parseFromInsight(securityType).name());
                this.securityInfoService.insert(info);
            } else {
                if ("02008".equals(message.get("SecuritySubType")) || "02004".equals(message.get("SecuritySubType"))
                        || "02003".equals(message.get("SecuritySubType"))
                ) {
                } else {
                    log.info(messageType + ":MdConstant interactionId=" + interactionId + ",MdConstant=" + message);
                }
            }

        } else if (marketData.hasMdTransaction()) {
            // 逐笔成交数据
            if (this.paramHandler.getDebug().isMarket())
                log.info(messageType + ":MdTransaction interactionId=" + interactionId + ",MdTransaction="
                        + ProtobufFormatter.formatMessage(marketData.getMdTransaction()));
        } else if (marketData.hasMdOrder()) {
            // 逐笔委托数据
            if (this.paramHandler.getDebug().isMarket())
                log.info(messageType + ":MdOrder interactionId=" + interactionId + ",MdOrder="
                        + ProtobufFormatter.formatMessage(marketData.getMdOrder()));
        } else if (marketData.hasMdIndicatorsRanking()) {
            // 指标排行版数据
            if (this.paramHandler.getDebug().isMarket())
                log.info(messageType + ":MdIndicatorsRanking interactionId=" + interactionId
                        + ",MdIndicatorsRanking=" + ProtobufFormatter.formatMessage(marketData.getMdIndicatorsRanking()));
        } else if (marketData.hasMdUpsDownsAnalysis()) {
            // 涨跌分析数据
            if (this.paramHandler.getDebug().isMarket())
                log.info(messageType + ":MdUpsDownsAnalysis interactionId=" + interactionId
                        + ",MdUpsDownsAnalysis=" + ProtobufFormatter.formatMessage(marketData.getMdUpsDownsAnalysis()));
        } else if (marketData.hasMdVolumeByPrice()) {
            // 量价分析行情数据
            if (this.paramHandler.getDebug().isMarket())
                log.info(messageType + ":MdVolumeByPrice interactionId=" + interactionId
                        + ",MdVolumeByPrice=" + ProtobufFormatter.formatMessage(marketData.getMdUpsDownsAnalysis()));
        } else if (marketData.hasMdKLine()) {
            Map<String, Object> message = ProtobufFormatter.formatMessage(marketData.getMdKLine());
            if(message.get("KLineCategory").equals(11)){
                if(this.paramHandler.getDebug().isMarket()) {
                    log.info("{} MdKLine: interactionId={},MdKLine={}", messageType.name(), interactionId, message);
                }
                if ("StockType".equals(message.get("securityType"))) {
                    this.marketDataRepository.saveMinuteKLine(messageType, EventType.STOCK_MINUTE, this.transferToStockMinute(message, true));
                } else if ("IndexType".equals(message.get("securityType"))) {
                    this.marketDataRepository.saveMinuteKLine(messageType, EventType.INDEX_MINUTE, this.transferToStockMinute(message, false));
                }
            }
        } else if (marketData.hasMdOption()) {
            // 期权行情数据
            if (this.paramHandler.getDebug().isMarket())
                log.info(messageType + ":MdOption interactionId=" + interactionId + ",MdOption="
                        + ProtobufFormatter.formatMessage(marketData.getMdOption()));
        } else if (marketData.hasMdTwap()) {
            // TWAP数值
            if (this.paramHandler.getDebug().isMarket())
                log.info(messageType + ":MdTwap interactionId=" + interactionId + ",MdTwap="
                        + ProtobufFormatter.formatMessage(marketData.getMdTwap()));
        } else if (marketData.hasMdFundFlowAnalysis()) {
            // 资金流向分析行情数据
            if (this.paramHandler.getDebug().isMarket())
                log.info(messageType + ":MdFundFlowAnalysis interactionId=" + interactionId
                        + ",MdFundFlowAnalysis="
                        + ProtobufFormatter.formatMessage(marketData.getMdFundFlowAnalysis()) + ",Size="
                        + marketData.toByteArray().length);
        } else if (marketData.hasMdETFBasicInfo()) {
            // ETF静态信息
            if (this.paramHandler.getDebug().isMarket())
                log.info(messageType + ":MdETFBasicInfo interactionId=" + interactionId
                        + ",MdETFBasicInfo=" + ProtobufFormatter.formatMessage(marketData.getMdETFBasicInfo()) + ",Size=" + marketData.toByteArray().length);
        }
    }

    private TickEntity transToTickEntity(Map message) {
        TickEntity tickEntity = new TickEntity();
        tickEntity.setStock(BizUtils.parseStockCodeFromSecurityId((String) message.get("HTSCSecurityID")));
        tickEntity.setDate((Integer) message.get("MDDate"));
        tickEntity.setTime((Integer) message.get("MDTime") / 1000);

        tickEntity.setTradingPhaseCode((String) message.get("TradingPhaseCode"));
        tickEntity.setMaxPx(Money.displayValue((Long) message.get("MaxPx"), 4));
        tickEntity.setMinPx(Money.displayValue((Long) message.get("MinPx"), 4));
        tickEntity.setPreClosePx(Money.displayValue((Long) message.get("PreClosePx"), 4));
        tickEntity.setNumTrades((Long) message.get("NumTrades"));
        tickEntity.setTotalVolumeTrade((Long) message.get("TotalVolumeTrade"));
        tickEntity.setTotalValueTrade(new BigDecimal(String.valueOf(message.get("TotalValueTrade"))));
        tickEntity.setLastPx(Money.displayValue((Long) message.get("LastPx"), 4));
        tickEntity.setOpenPx(Money.displayValue((Long) message.get("OpenPx"), 4));
        tickEntity.setClosePx(Money.displayValue((Long) message.get("ClosePx"), 4));
        tickEntity.setHighPx(Money.displayValue((Long) message.get("HighPx"), 4));
        tickEntity.setLowPx(Money.displayValue((Long) message.get("LowPx"), 4));

        tickEntity.setBuyPriceQueue(toDoubleList(message.get("BuyPriceQueue")));
        tickEntity.setBuyNumOrdersQueue(toLongList(message.get("BuyNumOrdersQueue")));
        tickEntity.setBuyOrderQtyQueue(toLongList(message.get("BuyOrderQtyQueue")));
        tickEntity.setSellPriceQueue(toDoubleList(message.get("SellPriceQueue")));
        tickEntity.setSellNumOrdersQueue(toLongList(message.get("SellNumOrdersQueue")));
        tickEntity.setSellOrderQtyQueue(toLongList(message.get("SellOrderQtyQueue")));
        return tickEntity;

    }

    private TickEntity transToIndexTickEntity(Map message) {
        TickEntity tickEntity = new TickEntity();
        tickEntity.setStock((String) message.get("HTSCSecurityID"));
        tickEntity.setDate((Integer) message.get("MDDate"));
        tickEntity.setTime((Integer) message.get("MDTime") / 100000);

        tickEntity.setTradingPhaseCode((String) message.get("TradingPhaseCode"));
        tickEntity.setPreClosePx(Money.displayValue((Long) message.get("PreClosePx"), 4));
        tickEntity.setNumTrades((Long) message.get("NumTrades"));
        tickEntity.setTotalVolumeTrade((Long) message.get("TotalVolumeTrade"));
        tickEntity.setTotalValueTrade(new BigDecimal(String.valueOf(message.get("TotalValueTrade"))));
        tickEntity.setLastPx(Money.displayValue((Long) message.get("LastPx"), 4));
        tickEntity.setOpenPx(Money.displayValue((Long) message.get("OpenPx"), 4));
        tickEntity.setClosePx(Money.displayValue((Long) message.get("ClosePx"), 4));
        tickEntity.setHighPx(Money.displayValue((Long) message.get("HighPx"), 4));
        tickEntity.setLowPx(Money.displayValue((Long) message.get("LowPx"), 4));
        return tickEntity;
    }

    private List<Long> toLongList(Object data) {
        if (null == data) {
            log.error("数据为null ");
            return Collections.EMPTY_LIST;
        }
        if (data instanceof Collection) {
            List<Long> ret = new ArrayList<>(((Collection) data).size());
            Iterator it = ((Collection) data).iterator();
            while (it.hasNext()) {
                ret.add((Long) it.next());
            }

            return ret;
        } else {
            log.error("error data :{}", data.toString());
            return Collections.EMPTY_LIST;
        }
    }

    private List<BigDecimal> toDoubleList(Object data) {
        if (null == data) {
            log.error("数据为null ");
            return Collections.EMPTY_LIST;
        }
        if (data instanceof Collection) {
            List<BigDecimal> ret = new ArrayList<>();
            Iterator it = ((Collection) data).iterator();
            while (it.hasNext()) {
                ret.add(Money.displayValue((Long) it.next(), 4));
            }
            return ret;
        } else {
            log.error("error data :{}", data.toString());
            return Collections.EMPTY_LIST;
        }
    }

    private StockMinute transferToStockMinute(Map<String, Object> message, boolean isStock) {
        StockMinute record = new StockMinute();
        record.setGmtArrived(DateUtils.now());
        record.setSecurityId((String)message.get("HTSCSecurityID"));
        record.setDealDate((Integer) message.get("MDDate"));
        record.setDealTime((Integer)message.get("MDTime")/100000);
        record.setClosePx(Money.displayValue((Long)message.get("ClosePx"), 4));
        record.setOpenPx(Money.displayValue((Long)message.get("OpenPx"), 4));
        record.setHighPx(Money.displayValue((Long)message.get("HighPx"), 4));
        record.setLowPx(Money.displayValue((Long)message.get("LowPx"), 4));
        record.setTradeValue(new BigDecimal(String.valueOf(message.get("TotalValueTrade"))));
        record.setTradeVolume(Long.parseLong(String.valueOf(message.get("TotalVolumeTrade"))));
        return record;
    }
}
