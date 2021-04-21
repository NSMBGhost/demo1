package com.example.demo.service;

import com.example.demo.bean.OnlineStock;
import com.example.demo.bean.PauseStock;
import com.example.demo.example.OnlineStockExample;
import com.example.demo.example.PauseStockExample;
import com.example.demo.mapper.OnlineStockMapper;
import com.example.demo.mapper.PauseStockMapper;
import com.example.demo.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OnlineStockService implements DailyChecker {

    @Autowired
    private OnlineStockMapper onlineStockMapper;

    @Autowired
    private PauseStockMapper pauseStockMapper;

    public Set<String> queryOnlineStocksByDate(int dateKey) {
        OnlineStockExample example = new OnlineStockExample();
        example.createCriteria().andDealDateEqualTo(dateKey);

        List<OnlineStock> dataList = this.onlineStockMapper.selectByExample(example);
        return dataList.stream().map(key -> {
            return key.getStockCode();
        }).collect(Collectors.toSet());
    }

    public Set<String> queryPauseStocksByDate(int dateKey) {
        PauseStockExample example = new PauseStockExample();
        example.createCriteria().andDealDateEqualTo(dateKey);

        List<PauseStock> dataList = this.pauseStockMapper.selectByExample(example);
        return dataList.stream().map(key -> {
            return key.getStockCode();
        }).collect(Collectors.toSet());
    }

    public Integer findLastTradeDay(String securityId, int dateKey) {
        OnlineStockExample example = new OnlineStockExample();
        example.createCriteria().andStockCodeEqualTo(securityId).andDealDateLessThan(dateKey);
        example.setOrderByClause("deal_date desc limit 1");
        List<OnlineStock> dataList = this.onlineStockMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        return dataList.get(0).getDealDate();
    }

    public List<Integer> queryForSecurityAndDataRange(String securityId, int dateKeyStart, int dateKeyEnd) {
        List<Integer> result = new ArrayList<>();
        OnlineStockExample example = new OnlineStockExample();
        example.createCriteria().andStockCodeEqualTo(securityId)
                .andDealDateGreaterThanOrEqualTo(dateKeyStart)
                .andDealDateLessThanOrEqualTo(dateKeyEnd);
        example.setOrderByClause("deal_date asc");
        List<OnlineStock> dataList = this.onlineStockMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(dataList)) {
            return result;
        }
        return dataList.stream().map(key -> {
            return key.getDealDate();
        }).collect(Collectors.toList());
    }

    public int insert(OnlineStock record) {
        Timestamp now = DateUtils.now();
        record.setGmtCreate(now);
        record.setGmtModified(now);
        return this.onlineStockMapper.insert(record);
    }

    public synchronized int insertBatch(List<OnlineStock> dataList) {
        Timestamp now = DateUtils.now();
        dataList.forEach(key -> {
            key.setGmtCreate(now);
            key.setGmtModified(now);
        });
        return this.onlineStockMapper.batchInsert(dataList);
    }

    @Override
    public long countByDate(int dateKey) {
        OnlineStockExample example = new OnlineStockExample();
        example.createCriteria().andDealDateEqualTo(dateKey);
        return this.onlineStockMapper.countByExample(example);
    }

    public int removeOnlineStock(int dateKey, String securityId) {
        OnlineStockExample example = new OnlineStockExample();
        example.createCriteria().andDealDateEqualTo(dateKey).andStockCodeEqualTo(securityId);
        return this.onlineStockMapper.deleteByExample(example);
    }

    public int removePauseStock(int dateKey, String securityId) {
        PauseStockExample example = new PauseStockExample();
        example.createCriteria().andDealDateEqualTo(dateKey).andStockCodeEqualTo(securityId);
        return this.pauseStockMapper.deleteByExample(example);
    }
}
