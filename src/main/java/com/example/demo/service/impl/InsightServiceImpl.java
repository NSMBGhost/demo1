package com.example.demo.service.impl;

import com.example.demo.bean.MarketDataType;
import com.example.demo.bean.SecurityInfo;
import com.example.demo.plugins.OnlineStockRepository;
import com.example.demo.service.MarketDataService;
import com.example.demo.utils.DateUtils;
import com.htsc.mdc.gateway.client.utils.ServerHost;
import com.htsc.mdc.insight.common.message.MessageIdProducer;
import com.htsc.mdc.insight.common.utils.EWaitStrategyType;
import com.htsc.mdc.insight.model.EMarketDataTypeProto;
import com.htsc.mdc.insight.model.MDPlaybackProto;
import com.htsc.mdc.insight.model.SecuritySourceTypeProtos;
import com.htsc.mdc.model.ESecurityIDSourceProtos;
import com.htsc.mdc.model.ESecurityTypeProtos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.htsc.mdc.gateway.client.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import com.lmax.disruptor.*;
@Service
@Slf4j
public class InsightServiceImpl implements MarketDataService {
    private static final AtomicBoolean RUN_FLAGS = new AtomicBoolean(false);
    private Map<MarketDataType, Long> lastInteractionMap = new ConcurrentHashMap<>();

    /**
     * 网关客户端启动器
     */
    private static MdcGatewayClientStarter mdcClient = null;

    @Value("${insight.server.master}")
    String serverMaster;
    @Value("#{'${insight.server.backup}'.split(',')}")
    List<String> backHostL;

    @Value("${insight.auth.account}")
    String serverId;
    @Value("${insight.auth.password}")
    String password;

    @Value("${insight.client.wait-strategy}")
    int waitStrategyType;
    @Value("${insight.client.buffer-size}")
    int clientBufferSize;
    @Value("${insight.client.threads}")
    int clientThreads;

    @Value("${insight.subscribe.tick}")
    boolean subscribeTick;
    @Value("${insight.subscribe.minute}")
    boolean subscribeMinute;

    @Autowired
    private OnRecvMarketData onRecvMarketData;

    @Autowired
    private OnlineStockRepository onlineStockRepository;

    public void startServer() {
        if (RUN_FLAGS.get()) {
            log.info("Server is already running a.");
            return;
        }

        connectServer();

        RUN_FLAGS.set(true);
        log.info("Server is  running .");
    }

    /**
     * 说明：连接服务器 : void
     */
    public void connectServer() {
        ServerHost server = ServerHost.buildFromString(serverMaster);
        final List<ServerHost> backupServerHosts = new ArrayList<>();
        if (null != backHostL) {
            for (String hostString : backHostL) {
                ServerHost tempHost = ServerHost.buildFromString(hostString);
                backupServerHosts.add(tempHost);
            }
        }
        // 设置使用ssl证书并指定证书位置 (外部客户必须使用证书)
        InsightCLientConfig.setUseSSL(true);
//        ClassPathResource crtResource = new ClassPathResource("classpath:ssl/HTInsightCA.crt");
//        InsightCLientConfig.setSslRootFileFilePath(crtResource.getPath());
        InsightCLientConfig.setTcpIdleTime(15);
        // 设置接收端底层buffer的大小，单位为B
        InsightCLientConfig.setNettyRecvBufferSize(8 * 1024 * 1024);
        try {
            // 网关客户端启动器初始化
            MdcGatewayClientStarter MdcClient = new MdcGatewayClientStarter();
            // 添加观察者，用来接收订阅/回放的行情数据
            // 此处的OnRecvMarketData为demo扩展
            MdcClient.addWatcher(onRecvMarketData);

            WaitStrategy waitStrategy = null;
            if (waitStrategyType == EWaitStrategyType.BusySpinType.getTypeValue()) {
                waitStrategy = new BusySpinWaitStrategy();
            } else if (waitStrategyType == EWaitStrategyType.YieldingType.getTypeValue()) {
                waitStrategy = new YieldingWaitStrategy();
            } else if (waitStrategyType == EWaitStrategyType.SleepingType.getTypeValue()) {
                waitStrategy = new SleepingWaitStrategy();
            } else {
                waitStrategy = new BlockingWaitStrategy();
            }

            // 网关客户端开始启动
            MdcClient.startTcpClient(clientBufferSize,
                    clientThreads, waitStrategy, server, serverId, password,
                    backupServerHosts);
            // 启动后因网关对客户端进行身份验证，几秒后便可发送订阅/回放请求
            mdcClient = MdcClient;
            // 等待一段时间
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stopServer() {
        if (null != mdcClient) {
            mdcClient.shutdown();
        }
        RUN_FLAGS.set(false);
        mdcClient = null;
        log.warn("shut down server ");
    }

    @Override
    public void processSubscribeAfterConnected() {
        this.queryAllMDConstant();
        this.queryAllIndexConstant();

        if (this.subscribeTick) {
            this.subscribeMarketByType(MarketDataType.MD_TICK);
        }
        if(this.subscribeMinute) {
            this.subscribeMarketByType(MarketDataType.MD_KLINE_1MIN);
        }
    }

    @Override
    public boolean subscribeMarketByType(MarketDataType marketDataType) {
        if (null == mdcClient) {
            startServer();
        }

        Long interactionId = this.lastInteractionMap.get(marketDataType);
        if(Objects.nonNull(interactionId)) {
            mdcClient.getRequestor().cancelSubscribe(interactionId);
        }

        interactionId = MessageIdProducer.getMessageId();

        Set<EMarketDataTypeProto.EMarketDataType> marketDataTypes = new HashSet<>();
        // TICK行情
        marketDataTypes.add(this.convertMarketDataType(marketDataType));
//        // 逐笔成交
//        marketDataTypes.add(EMarketDataTypeProto.EMarketDataType.MD_TRANSACTION);
//        // 逐笔委托
//        marketDataTypes.add(EMarketDataTypeProto.EMarketDataType.MD_ORDER);
//        // 订阅分钟K线
//        marketDataTypes.add(EMarketDataTypeProto.EMarketDataType.MD_KLINE_1MIN);

        // 根据证券ID集合订阅行情
        boolean result = mdcClient.getRequestor().subscribeMarketDataByAll(interactionId, marketDataTypes);

        log.info("SubscribeByType {}, result is {}", marketDataTypes, result);
        if(result) {
            this.lastInteractionMap.put(marketDataType, interactionId);
        }
        return result;
    }

    /**
     * 按照ID来订阅
     */
    @Override
    public boolean subscribeMarketByCodeSet(MarketDataType marketDataType, Set<String> codeSet) {
        if (null == mdcClient) {
            startServer();
        }

        Long interactionId = this.lastInteractionMap.get(marketDataType);
        if(Objects.nonNull(interactionId)) {
            mdcClient.getRequestor().cancelSubscribe(interactionId);
        }

        // 订阅证券ID集合
        Set<EMarketDataTypeProto.EMarketDataType> marketDataTypes = new HashSet<>();
        // 订阅分钟K线
        marketDataTypes.add(this.convertMarketDataType(marketDataType));

        // 根据证券ID集合订阅行情
        boolean result = mdcClient.getRequestor().addSubscribeMarketDataByID(interactionId, marketDataTypes, codeSet);
        log.info("SubscribeByCode {}, result is {}", marketDataTypes, result);
        if(result) {
            this.lastInteractionMap.put(marketDataType, interactionId);
        }
        return result;
    }

    private EMarketDataTypeProto.EMarketDataType convertMarketDataType(MarketDataType marketDataType) {
        switch (marketDataType) {
            case MD_CONSTANT:
                return EMarketDataTypeProto.EMarketDataType.MD_CONSTANT;
            case MD_TICK:
                return EMarketDataTypeProto.EMarketDataType.MD_TICK;
            case MD_KLINE_1MIN:
                return EMarketDataTypeProto.EMarketDataType.MD_KLINE_1MIN;
            case MD_ORDER:
                return EMarketDataTypeProto.EMarketDataType.MD_ORDER;
            case MD_TRANSACTION:
                return EMarketDataTypeProto.EMarketDataType.MD_TRANSACTION;
            default:
                return EMarketDataTypeProto.EMarketDataType.UNKNOWN_DATA_TYPE;
        }
    }

    @Override
    public void replayMd(Set<String> codeSet) {
        // 订阅/回放的证券类型集合
        startServer();
        Set<SecuritySourceTypeProtos.SecuritySourceType> eSecurityTypes = new HashSet<>();
        // 回放历史行情数据
        String taskId = UUID.randomUUID().toString();
        long interactionId = MessageIdProducer.getMessageId();

        String strToday = DateUtils.formatNow(DateUtils.DATE_FORMAT_YEAR_MONTH_DAY);
        mdcClient.getRequestor().replayHisMarketData(interactionId, taskId, codeSet, DateUtils.parseDate(strToday +" 09:00:00", DateUtils.DATE_FORMAT_DEFAULT), DateUtils.parseDate(strToday + " 15:00:00", DateUtils.DATE_FORMAT_DEFAULT), EMarketDataTypeProto.EMarketDataType.MD_KLINE_1MIN,
                eSecurityTypes, MDPlaybackProto.EPlaybackExrightsType.NO_EXRIGHTS);
    }

    /**
     * 订阅股票基础信息
     */
    @Override
    public void queryAllMDConstant() {
        startServer();

        long interactionId = MessageIdProducer.getMessageId();
        Set<String> htscSecurityIDs = new HashSet<>();
        Set<SecuritySourceTypeProtos.SecuritySourceType> securitySourceTypes = new HashSet<>();
        SecuritySourceTypeProtos.SecuritySourceType shStock = SecuritySourceTypeProtos.SecuritySourceType.newBuilder()
                .setSecurityIDSource(ESecurityIDSourceProtos.ESecurityIDSource.XSHE)
                .setSecurityType(ESecurityTypeProtos.ESecurityType.StockType).build();
        SecuritySourceTypeProtos.SecuritySourceType szStock = SecuritySourceTypeProtos.SecuritySourceType.newBuilder()
                .setSecurityIDSource(ESecurityIDSourceProtos.ESecurityIDSource.XSHG)
                .setSecurityType(ESecurityTypeProtos.ESecurityType.StockType).build();

        securitySourceTypes.add(shStock);
        securitySourceTypes.add(szStock);

        mdcClient.getRequestor().queryLatestMDConstant(interactionId, securitySourceTypes, htscSecurityIDs);
    }

    /**
     * 订阅指数实时信息
     */
    @Override
    public void queryAllIndexConstant() {
        startServer();
        long interactionId = MessageIdProducer.getMessageId();
        Set<SecurityInfo> securitySet = this.onlineStockRepository.getNeedIndexSet();
        Set<SecuritySourceTypeProtos.SecuritySourceType> securitySourceTypes = new HashSet<>();

        Set<String> securityIdSet = securitySet.stream().map(key -> {
            return key.getSecurityId();
        }).collect(Collectors.toSet());
        // 通过onMDQueryResponse来应答,interactionId会和发送时的一致
        mdcClient.getRequestor().queryLatestMDConstant(interactionId, securitySourceTypes, securityIdSet);
    }
}
