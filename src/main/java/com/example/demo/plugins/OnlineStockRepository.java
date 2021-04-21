package com.example.demo.plugins;

import com.example.demo.bean.*;
import com.example.demo.mapper.PauseStockMapper;
import com.example.demo.service.*;
import com.example.demo.utils.BizUtils;
import com.example.demo.utils.DateUtils;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.netty.util.internal.ConcurrentSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class OnlineStockRepository {
    private Set<String> needIndexSet = new HashSet<>(Arrays.asList("000001.SH,000016.SH,000903.SH,399001.SZ,399006.SZ,000009.SH,000300.SH,000905.SH,399005.SZ,000906.SH,000852.SH".split(",")));
//    private final Set<String> indexSet = new HashSet<>(Arrays.asList("000001.SH,000300.SH,000852.SH,000905.SH,399001.SZ".split(",")));

    @Autowired
    private IBaseInfoService dataFiller;

    @Autowired
    private TradeDayRepository tradeDayRepository;

    @Autowired
    private StockMinuteService stockMinuteService;

    @Autowired
    private SecurityInfoService securityInfoService;

    @Autowired
    private OnlineStockService onlineStockService;

    @Autowired
    private PauseStockMapper pauseStockMapper;

    private LoadingCache<Integer, Set<String>> onlineStockCache = null;
    private LoadingCache<Integer, Set<String>> pauseStockCache = null;

    public OnlineStockRepository() {
        CacheLoader<Integer, Set<String>> onlineCacheLoader = new CacheLoader<Integer, Set<String>>() {
            @Override
            public Set<String> load(Integer dateKey) throws Exception {
                Preconditions.checkNotNull(dateKey);
                return onlineStockService.queryOnlineStocksByDate(dateKey);
            }
        };
        this.onlineStockCache = CacheBuilder.newBuilder().maximumSize(Strings.CACHE_CAPACITY)
                .expireAfterWrite(Strings.CACHE_EXPIRE_TIME, TimeUnit.SECONDS).build(onlineCacheLoader);

        CacheLoader<Integer, Set<String>> pauseCacheLoader = new CacheLoader<Integer, Set<String>>() {
            @Override
            public Set<String> load(Integer dateKey) throws Exception {
                Preconditions.checkNotNull(dateKey);
                return onlineStockService.queryPauseStocksByDate(dateKey);
            }
        };
        this.pauseStockCache = CacheBuilder.newBuilder().maximumSize(Strings.CACHE_CAPACITY)
                .expireAfterWrite(Strings.CACHE_EXPIRE_TIME, TimeUnit.SECONDS).build(pauseCacheLoader);
    }

    public Set<SecurityInfo> getNeedIndexSet() {
        return this.needIndexSet.stream().map(key -> {
            return this.securityInfoService.get(key);
        }).collect(Collectors.toSet());
    }

    public void refreshSkipStock() {
        int dateKey = BizUtils.getDateIntValueForNow();
        Set<String> todaySkipStocks = this.queryPauseStocksByDate(dateKey);
        Set<SecurityInfo> todayOnlineStocks = this.queryOnlineStocksByDate(dateKey);
        for (SecurityInfo securityInfo : todayOnlineStocks) {
            if(todaySkipStocks.contains(securityInfo.getSecurityId())) {
                this.removeFromOnlineStock(dateKey, securityInfo.getSecurityId());
            }
        }

        List<StockMinute> entityList = this.stockMinuteService.queryStockAtTime(dateKey, 925);
        for (StockMinute entity : entityList) {
            String securityId = entity.getSecurityId();
            if(todaySkipStocks.contains(securityId)) {
                if(entity.getTradeVolume() > 0) {
                    this.removeFromPauseStock(dateKey, securityId);
                } else {
                    this.stockMinuteService.deleteByUniqueKey(dateKey, 925, securityId);
                }
            }
        }
    }

    public Set<SecurityInfo> queryOnlineStocksByDate(int dateKey) {
        Set<SecurityInfo> result = new ConcurrentSet<>();
        if(!this.tradeDayRepository.isTradeDay(dateKey)) {
            return new HashSet<>();
        }

        Set<String> codeSet = this.onlineStockCache.getUnchecked(dateKey);
        if(CollectionUtils.isEmpty(codeSet)) {
            try {
                codeSet = this.doFillOnlineStockForDate(dateKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(CollectionUtils.isNotEmpty(codeSet)) {
            for (String securityId : codeSet) {
                SecurityInfo securityInfo = this.securityInfoService.get(securityId);
                if(dateKey == BizUtils.convertDealDate(securityInfo.getEndDate())) {
                    continue;
                }
                result.add(securityInfo);
            }
        }
        return result;
    }

    public Set<String> queryPauseStocksByDate(int dateKey) {
        Set<String> codeSet = this.pauseStockCache.getUnchecked(dateKey);
        if(CollectionUtils.isEmpty(codeSet)) {
            String dealDate = BizUtils.convertDateKey(dateKey);
            try {
                codeSet = this.doFillPauseStockForDate(dealDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return codeSet;
    }

    private Set<String> doFillOnlineStockForDate(int dateKey) throws Exception {
        String dealDate = BizUtils.convertDateKey(dateKey);
        List<SecurityInfo> securityInfoList = this.dataFiller.queryAllSecurities(SecurityType.STOCK, dealDate);
        this.securityInfoService.batchInsert(securityInfoList);

        Set<String> pauseStocks = this.queryPauseStocksByDate(dateKey);

        List<OnlineStock> dataList = new ArrayList<>();
        for (SecurityInfo stockInfo : securityInfoList) {
            String securityId = stockInfo.getSecurityId();
            if(pauseStocks.contains(securityId)) {
                continue;
            }

            if(dateKey == BizUtils.convertDealDate(stockInfo.getEndDate())) {
                this.removeFromOnlineStock(dateKey, securityId);
                continue;
            }

            OnlineStock onlineStock = new OnlineStock();
            onlineStock.setDealDate(dateKey);
            onlineStock.setStockCode(securityId);
            dataList.add(onlineStock);
        }

        if(CollectionUtils.isNotEmpty(dataList)) {
            int result = this.onlineStockService.insertBatch(dataList);
            log.info("date: {}, online stock count: {}", dateKey, result);
        }

        return dataList.stream().map(key -> {
            return key.getStockCode();
        }).collect(Collectors.toSet());
    }

    private Set<String> doFillPauseStockForDate(String dealDate) throws Exception {
        Set<String> pauseStocks = this.dataFiller.queryPauseStocks(dealDate);

        int dateKey = BizUtils.convertDealDate(dealDate);
        Date now = DateUtils.now();
        List<PauseStock> pauseStockList = new ArrayList<>();
        for (String stockCode : pauseStocks) {
            PauseStock pauseStock = new PauseStock();
            pauseStock.setDealDate(dateKey);
            pauseStock.setGmtCreate(now);
            pauseStock.setGmtModified(now);
            pauseStock.setStockCode(stockCode);

            pauseStockList.add(pauseStock);
        }

        if(CollectionUtils.isNotEmpty(pauseStockList)) {
            int result = this.pauseStockMapper.batchInsert(pauseStockList);
            log.info("date: {}, pause stock count: {}", dateKey, result);

            for (PauseStock pauseStock : pauseStockList) {
                this.onlineStockService.removeOnlineStock(dateKey, pauseStock.getStockCode());
            }
            this.onlineStockCache.invalidate(dateKey);
            this.pauseStockCache.invalidate(dateKey);
        }

        return pauseStocks;
    }

    public void removeFromPauseStock(int dateKey, String securityId) {
        OnlineStock onlineStock = new OnlineStock();
        onlineStock.setDealDate(dateKey);
        onlineStock.setStockCode(securityId);
        this.onlineStockService.insert(onlineStock);

        this.onlineStockService.removePauseStock(dateKey, securityId);

        this.onlineStockCache.invalidate(dateKey);
        this.pauseStockCache.invalidate(dateKey);
    }

    public void removeFromOnlineStock(int dateKey, String securityId) {
        Date now = DateUtils.now();

        PauseStock pauseStock = new PauseStock();
        pauseStock.setDealDate(dateKey);
        pauseStock.setGmtCreate(now);
        pauseStock.setGmtModified(now);
        pauseStock.setStockCode(securityId);
        this.pauseStockMapper.insert(pauseStock);

        this.onlineStockService.removeOnlineStock(dateKey, securityId);

        this.onlineStockCache.invalidate(dateKey);
        this.pauseStockCache.invalidate(dateKey);
    }
}
