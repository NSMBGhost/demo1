package com.example.demo.service;

import com.example.demo.bean.SecurityInfo;
import com.example.demo.bean.SecurityType;
import com.example.demo.example.SecurityInfoExample;
import com.example.demo.mapper.SecurityInfoMapper;
import com.example.demo.plugins.TradeDayRepository;
import com.example.demo.utils.BizUtils;
import com.example.demo.utils.DateUtils;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SecurityInfoService {
    @Autowired
    private TradeDayRepository tradeDayRepository;

    @Autowired
    private IBaseInfoService dataFiller;

    @Autowired
    private SecurityInfoMapper securityInfoMapper;

    private LoadingCache<String, Optional<SecurityInfo>> idCache = null;

    public SecurityInfoService() {
        CacheLoader<String, Optional<SecurityInfo>> idCacheLoader = new CacheLoader<String, Optional<SecurityInfo>>() {
            @Override
            public Optional<SecurityInfo> load(String securityId) throws Exception {
                Preconditions.checkNotNull(securityId);
                SecurityInfo stockInfo = loadByPK(securityId);
                return Objects.isNull(stockInfo) ? Optional.empty() : Optional.of(stockInfo);
            }
        };
        this.idCache = CacheBuilder.newBuilder().maximumSize(Strings.CACHE_CAPACITY)
                .expireAfterWrite(1, TimeUnit.DAYS).build(idCacheLoader);
    }

    public SecurityInfo get(String securityId) {
        Optional<SecurityInfo> optional = this.idCache.getUnchecked(securityId);
        if(optional.isPresent()) {
            return optional.get();
        }

        try {
            SecurityInfo stockInfo = this.dataFiller.getSecurityInfo(securityId);
            if(Objects.nonNull(stockInfo)) {
                this.insert(stockInfo);
                this.idCache.refresh(securityId);
            }
            return stockInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SecurityInfo loadByPK(String securityId) {
        SecurityInfoExample example = new SecurityInfoExample();
        example.createCriteria().andSecurityIdEqualTo(securityId);

        List<SecurityInfo> dataList = this.securityInfoMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        return dataList.get(0);
    }

    public List<SecurityInfo> queryForList(SecurityType securityType) {
        SecurityInfoExample example = new SecurityInfoExample();
        example.createCriteria().andSecurityTypeEqualTo(securityType.name());

        return this.securityInfoMapper.selectByExample(example);
    }

    public long insert(SecurityInfo record) {
        Date now = DateUtils.now();
        SecurityInfo exists = this.loadByPK(record.getSecurityId());
        if(Objects.isNull(exists)) {
            record.setGmtCreate(now);
            record.setGmtModified(now);
            record.setLastUnlimitDay(this.findLastUnLimitDay(record.getSecurityId(), record.getStartDate()));
            return this.securityInfoMapper.insert(record);
        } else {
            exists.setGmtModified(now);
            exists.setDisplayName(record.getDisplayName());
            if(StringUtils.isNumeric(record.getStartDate())) {
                exists.setStartDate(BizUtils.convertDateKey(Integer.parseInt(record.getStartDate())));
            } else {
                exists.setStartDate(record.getStartDate());
            }

            exists.setEndDate(record.getEndDate().compareTo(exists.getEndDate()) < 0 ? record.getEndDate() : exists.getEndDate());
            if(exists.getLastUnlimitDay() == null) {
                exists.setLastUnlimitDay(this.findLastUnLimitDay(record.getSecurityId(), record.getStartDate()));
            }
            int result = this.securityInfoMapper.updateByPrimaryKeySelective(exists);
            return result;
        }
    }

    public synchronized int batchInsert(List<SecurityInfo> dataList) {
        Date now = DateUtils.now();
        dataList.forEach(key -> {
            key.setGmtCreate(now);
            key.setGmtModified(now);
            if(StringUtils.isNotBlank(key.getStartDate())) {
                key.setLastUnlimitDay(this.findLastUnLimitDay(key.getSecurityId(), key.getStartDate()));
            }
        });
        return this.securityInfoMapper.batchInsert(dataList);
    }

    public int updateUnLimitTradeDay() {
        int rowCount = 0;
        SecurityInfoExample example = new SecurityInfoExample();
        List<SecurityInfo> dataList = this.securityInfoMapper.selectByExample(example);
        for (SecurityInfo record : dataList) {
            this.updateLastUnLimitDay(record.getId(), this.findLastUnLimitDay(record.getSecurityId(), record.getStartDate()));
            rowCount ++;
        }
        return rowCount;
    }

    private void updateLastUnLimitDay(Long id, Integer unLimitDay) {
        SecurityInfo record = new SecurityInfo();
        record.setId(id);
        record.setLastUnlimitDay(unLimitDay);
        this.securityInfoMapper.updateByPrimaryKeySelective(record);
    }

    private int findLastUnLimitDay(String securityId, String listingDate) {
        if(StringUtils.isBlank(listingDate)) {
            return 0;
        }

        int dateKey = BizUtils.convertDealDate(listingDate);
        if(securityId.startsWith("68") && dateKey >= 20190722) {

        } else if(securityId.startsWith("30") && dateKey >= 20200824) {

        } else {
            return BizUtils.convertDealDate(listingDate);
        }

        String lastUnLimitDay = this.tradeDayRepository.getTradeDayNextRange(listingDate, 4);
        return BizUtils.convertDealDate(lastUnLimitDay);
    }
}
