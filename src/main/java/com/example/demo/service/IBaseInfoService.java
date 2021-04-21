package com.example.demo.service;

import com.example.demo.bean.SecurityInfo;
import com.example.demo.bean.SecurityType;

import java.util.List;
import java.util.Set;

public interface IBaseInfoService {
    List<String> queryAllTradeDays() throws Exception;

    List<SecurityInfo> queryAllSecurities(SecurityType securitiesType, String dealDate) throws Exception;
    SecurityInfo getSecurityInfo(String securityId) throws Exception;
    Set<String> queryPauseStocks(String dealDate) throws Exception;
}
