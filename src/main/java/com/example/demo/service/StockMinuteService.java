package com.example.demo.service;

import com.example.demo.bean.StockMinute;
import com.example.demo.example.StockMinuteExample;
import com.example.demo.mapper.StockMinuteMapper;
import com.example.demo.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class StockMinuteService implements DailyChecker, DailyDeleter, PauseStockCleaner {

    @Autowired
    private OnlineStockService onlineStockService;

    @Autowired
    private StockMinuteMapper stockMinuteMapper;

    public void insert(StockMinute record) {
        Timestamp now = DateUtils.now();
        record.setGmtCreate(now);
        record.setGmtModified(now);
        this.stockMinuteMapper.insert(record);
    }

    public synchronized int batchInsert(List<StockMinute> recordList) {
        Timestamp now = DateUtils.now();
        for (StockMinute record : recordList) {
            if(Objects.isNull(record.getGmtCreate())) {
                record.setGmtCreate(now);
            }
            if(Objects.isNull(record.getGmtModified())) {
                record.setGmtModified(now);
            }
        }
        return this.stockMinuteMapper.batchInsert(recordList);
    }

    public List<StockMinute> queryStockAtTime(int dateKey, int timeKey) {
        StockMinuteExample example = new StockMinuteExample();
        example.createCriteria().andDealDateEqualTo(dateKey).andDealTimeEqualTo(timeKey);
        return this.stockMinuteMapper.selectByExample(example);
    }

    public List<StockMinute> queryBySecurityId(int dateKey, String securityId) {
        StockMinuteExample example = new StockMinuteExample();
        example.createCriteria().andDealDateEqualTo(dateKey).andSecurityIdEqualTo(securityId);
        return this.stockMinuteMapper.selectByExample(example);
    }

    @Override
    public int deletePauseStocks(int dateKey) {
        int rowCount = 0;
        List<StockMinute> minuteList = this.queryStockAtTime(dateKey, 930);
        Set<String> onlineSecuritySet = this.onlineStockService.queryOnlineStocksByDate(dateKey);
        for (StockMinute stockMinute : minuteList) {
            if(onlineSecuritySet.contains(stockMinute.getSecurityId())) {
                continue;
            }
            this.deleteBySecurityId(dateKey, stockMinute.getSecurityId());
            rowCount ++;
        }
        return rowCount;
    }

    public int deleteBySecurityId(int dateKey, String securityId) {
        StockMinuteExample example = new StockMinuteExample();
        example.createCriteria().andDealDateEqualTo(dateKey)
                .andSecurityIdEqualTo(securityId);
        return this.stockMinuteMapper.deleteByExample(example);
    }

    public int deleteByUniqueKey(int dateKey, int dealTime, String securityId) {
        StockMinuteExample example = new StockMinuteExample();
        example.createCriteria().andDealDateEqualTo(dateKey)
                .andDealTimeEqualTo(dealTime)
                .andSecurityIdEqualTo(securityId);
        return this.stockMinuteMapper.deleteByExample(example);
    }

    @Override
    public long countByDate(int dateKey) {
        StockMinuteExample example = new StockMinuteExample();
        example.createCriteria().andDealDateEqualTo(dateKey).andDealTimeEqualTo(930);
        return this.stockMinuteMapper.countByExample(example);
    }

    public long deleteByDate(int dateKey) {
        StockMinuteExample example = new StockMinuteExample();
        example.createCriteria().andDealDateEqualTo(dateKey);
        return this.stockMinuteMapper.deleteByExample(example);
    }
}
