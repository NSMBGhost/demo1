package com.example.demo.plugins;

import com.example.demo.service.IBaseInfoService;
import com.example.demo.service.Strings;
import com.example.demo.service.TradeDayService;
import com.example.demo.utils.BizUtils;
import com.example.demo.utils.DateUtils;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Repository
public class TradeDayRepository {
    private List<String> allTradeDays = new ArrayList<>();

    @Autowired
    private IBaseInfoService dataFiller;

    @Autowired
    private TradeDayService tradeDayService;

    private LoadingCache<Integer, Integer> lastTradeDayCache = null;

    public TradeDayRepository() {
        CacheLoader<Integer, Integer> idCacheLoader = new CacheLoader<Integer, Integer>() {
            @Override
            public Integer load(Integer dateKey) throws Exception {
                Preconditions.checkNotNull(dateKey);
                return tradeDayService.findLastTradeDayBefore(dateKey);
            }
        };
        this.lastTradeDayCache = CacheBuilder.newBuilder().maximumSize(Strings.CACHE_CAPACITY)
                .expireAfterWrite(1, TimeUnit.DAYS).build(idCacheLoader);
    }

    public List<String> getAllTradeDays() {
        if(CollectionUtils.isEmpty(this.allTradeDays)) {
            this.allTradeDays = this.tradeDayService.queryForList();
            if(CollectionUtils.isEmpty(this.allTradeDays)) {
                try {
                    this.allTradeDays = this.dataFiller.queryAllTradeDays();
                    this.tradeDayService.batchInsert(this.allTradeDays);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return this.allTradeDays;
    }

    public Integer findLastTradeDayBefore(int dateKey) {
        try {
            return lastTradeDayCache.get(dateKey);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isTradeDay() {
        String dealDate = DateUtils.formatNow(DateUtils.DATE_FORMAT_YEAR_MONTH_DAY);
        return isTradeDay(dealDate);
    }

    public boolean isTradeDay(String dealDate) {
        return this.getAllTradeDays().contains(dealDate);
    }

    public boolean isTradeDay(int dateKey) {
        return isTradeDay(BizUtils.convertDateKey(dateKey));
    }

    public void range(String startDate, String endDate, Function<Integer, Void> function) {
        String strBeginDate = startDate;
        String strNow = DateUtils.formatNow(DateUtils.DATE_FORMAT_YEAR_MONTH_DAY);
        while(strBeginDate.compareTo(strNow) <= 0 && strBeginDate.compareTo(endDate) <= 0) {
            if(this.isTradeDay(strBeginDate)) {
                int dateKey = BizUtils.convertDealDate(strBeginDate);
                function.apply(dateKey);
            }

            strBeginDate = this.getTradeDayNextRange(strBeginDate, 1);
        }
    }

    public String getTradeDayNextRange(String startDate, int offset) {
        List<String> days = this.getAllTradeDays();

        int rowCount = 0;
        String lastDate = null;
        boolean found = false;
        if(CollectionUtils.isNotEmpty(days)) {
            for(String tradeDay: days) {
                if(startDate.compareTo(tradeDay) == 0) {
                    found = true;
                } else if(startDate.compareTo(tradeDay) < 0 || found) {
                    lastDate = tradeDay;
                    rowCount ++;
                }

                if(rowCount >= offset) {
                    break;
                }
            }
        }

        if(0 == rowCount || StringUtils.isBlank(lastDate)) {
            Date nextDate = DateUtils.addDays(DateUtils.parseDate(startDate, DateUtils.DATE_FORMAT_YEAR_MONTH_DAY), offset);
            lastDate = DateUtils.formatTime(nextDate, DateUtils.DATE_FORMAT_YEAR_MONTH_DAY);
        }
        return lastDate;
    }

}
