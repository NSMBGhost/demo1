package com.example.demo.service;

import com.example.demo.bean.TradeDay;
import com.example.demo.example.TradeDayExample;
import com.example.demo.mapper.TradeDayMapper;
import com.example.demo.utils.BizUtils;
import com.example.demo.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TradeDayService {
    @Autowired
    private TradeDayMapper tradeDayMapper;

    public int batchInsert(List<String> dayList) {
        List<TradeDay> recordList = new ArrayList<>();
        Date now = DateUtils.now();
        for (String dealDate : dayList) {
            TradeDay record = new TradeDay();
            record.setGmtCreate(now);
            record.setDealDate(dealDate);
            recordList.add(record);
        }
        return this.tradeDayMapper.batchInsert(recordList);
    }

    public Integer findLastTradeDayBefore(int dateKey) {
        String dealDate = BizUtils.convertDateKey(dateKey);
        TradeDayExample example = new TradeDayExample();
        example.createCriteria().andDealDateLessThan(dealDate);
        example.setOrderByClause(" deal_date desc limit 1");
        List<TradeDay> dataList = this.tradeDayMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        return BizUtils.convertDealDate(dataList.get(0).getDealDate());
    }

    public List<String> queryForList() {
        TradeDayExample example = new TradeDayExample();
        example.setOrderByClause(" deal_date asc");
        List<TradeDay> dataList = this.tradeDayMapper.selectByExample(example);
        return dataList.stream().map(key -> {
            return key.getDealDate();
        }).collect(Collectors.toList());
    }
}
