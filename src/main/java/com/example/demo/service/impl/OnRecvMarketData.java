package com.example.demo.service.impl;

import com.example.demo.bean.MessageType;
import com.example.demo.service.MarketDataService;
import com.google.protobuf.TextFormat;
import com.htsc.mdc.gateway.client.watcher.OnMarketData;
import com.htsc.mdc.insight.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
    public final class OnRecvMarketData implements OnMarketData {

        @Autowired
        private MarketDataDispatcher marketDataDispatcher;

        @Autowired
        private MarketDataService marketDataService;

        @Override
        public void debug(String logs) {
            // TODO Auto-generated method stub
            log.debug("OnDebug " + logs);
        }

        @Override
        public void info(String logs) {
            // TODO Auto-generated method stub
            log.info("OnInfo " + logs);
        }

        @Override
        public void warn(String logs) {
            // TODO Auto-generated method stub
            log.info("warn " + logs);

        }

        @Override
        public void error(String logs) {
            // TODO Auto-generated method stub
            log.info("error " + logs);
        }

        @Override
        public void onException(Throwable e) {
            // TODO Auto-generated method stub
            log.error("onException", e);
        }

        @Override
        public void onChannelClosed(Throwable e) {
            // TODO Auto-generated method stub
            log.info("onChannelClosed " + e);
        }

        @Override
        public void onNoServers() {
            // TODO Auto-generated method stub
            log.error("onNoServers ");
        }

        @Override
        public void onNoConnections() {
            // TODO Auto-generated method stub
            log.info("onNoConnections ");
        }

        @Override
        public void onGeneralError(long interactionId, InsightErrorContextProtos.InsightErrorContext generalErrorMessage) {
            // TODO Auto-generated method stub
            log.info("onGeneralError interactionId=" + interactionId + ",generalErrorMessage="
                    + TextFormat.shortDebugString(generalErrorMessage));
        }

        @Override
        public void onServiceDiscoveryResponse(long interactionId,
                                               ServiceDiscoveryProto.ServiceDiscoveryResponse serviceDiscoveryResponse) {
            // TODO Auto-generated method stub
            log.info("onServiceDiscoveryResponse interactionId=" + interactionId
                    + ",serviceDiscoveryResponse=" + TextFormat.shortDebugString(serviceDiscoveryResponse));
        }

        @Override
        public void onMDSubscribeResponse(long interactionId, MDSubscribeProtos.MDSubscribeResponse mdSubscribeResponse) {
            // TODO Auto-generated method stub
            log.info("onMDSubscribeResponse interactionId=" + interactionId + ",mdSubscribeResponse="
                    + TextFormat.shortDebugString(mdSubscribeResponse));
        }

        private void onReceiveMarketData(MessageType messageType, long interactionId, MarketDataProto.MarketData marketData) {
            marketDataDispatcher.onReceiveMarketData(messageType, interactionId, marketData);
        }

        @Override
        public void onPushMarketData(long interactionId, MarketDataProto.MarketData pushMarketData) {
            // TODO Auto-generated method stub
            this.onReceiveMarketData(MessageType.PUSH_MARKET_DATA, interactionId, pushMarketData);
        }

        @Override
        public void onMDQueryResponse(long interactionId, MDQueryProto.MDQueryResponse mdQueryResponse) {
            // TODO Auto-generated method stub
            MarketDataProto.MarketDataStream mdStream = mdQueryResponse.getMarketDataStream();
            MarketDataProto.MarketDataList mdList = mdStream.getMarketDataList();
            log.info("onMDQueryResponse interactionId=" + interactionId
                            + ",QueryType={},IsSuccess={},MarketDatasCount={},IsFinished={},Serial={}",
                    mdQueryResponse.getQueryType(), mdQueryResponse.getIsSuccess(), mdList.getMarketDatasCount(),
                    mdStream.getIsFinished(), mdStream.getSerial());

            List<MarketDataProto.MarketData> mdsList = mdList.getMarketDatasList();
            for (MarketDataProto.MarketData marketData : mdsList) {
                this.onReceiveMarketData(MessageType.MD_QUERY_RESPONSE, interactionId, marketData);
            }
        }

        @Override
        public void onPlaybackResponse(long interactionId, MDPlaybackProto.PlaybackResponse playbackResponse) {
            // TODO Auto-generated method stub
            log.info("onPlaybackResponse interactionId=" + interactionId + ",playbackResponse="
                    + TextFormat.shortDebugString(playbackResponse));
        }

        @Override
        public void onPlaybackControlResponse(long interactionId, MDPlaybackProto.PlaybackControlResponse playbackControlResponse) {
            // TODO Auto-generated method stub
            log.info("onPlaybackControlResponse interactionId=" + interactionId + ",playbackControlResponse="
                    + TextFormat.shortDebugString(playbackControlResponse));
        }

        @Override
        public void onPlaybackStatus(long interactionId, MDPlaybackProto.PlaybackStatus playbackStatus) {
            // TODO Auto-generated method stub
            log.info("onPlaybackStatus interactionId=" + interactionId + ",playbackStatus="
                    + TextFormat.shortDebugString(playbackStatus));
            playbackStatus.getTaskStatus();
        }

        @Override
        public void onPlaybackPayload(long interactionId, MDPlaybackProto.PlaybackPayload playbackPayload) {
            // TODO Auto-generated method stub
            log.info("onPlaybackPayload interactionId=" + interactionId + ",playbackPayload="
                    + playbackPayload.getTaskId() + ",Numbers="
                    + playbackPayload.getMarketDataStream().getMarketDataList().getMarketDatasCount());
            for (MarketDataProto.MarketData marketData : playbackPayload.getMarketDataStream().getMarketDataList()
                    .getMarketDatasList()) {
                this.onReceiveMarketData(MessageType.PLAYBACK_PAYLOAD, interactionId, marketData);
            }
        }

        @Override
        public void onLoginDiscoverySuccess(String serverHost) {
            // TODO Auto-generated method stub
            log.info("onLoginDiscoverySuccess serverHost=" + serverHost);

        }

        @Override
        public void onLoginDiscoveryFailed(Throwable errorContext) {
            // TODO Auto-generated method stub
            log.info("onLoginDiscoveryFailed InsightErrorContext=" + errorContext);
        }

        @Override
        public void onLoginDiscoveryResponse(long interactionId, LoginProto.LoginResponse loginResponse) {
            // TODO Auto-generated method stub
            log.info("onLoginDiscoveryResponse interactionId=" + interactionId + ",LoginResponse="
                    + TextFormat.shortDebugString(loginResponse));
        }

        @Override
        public void onLoginDataServiceSuccess(String serverHost) {
            log.info("onLoginDataServiceSuccess serverHost=" + serverHost);

            this.marketDataService.processSubscribeAfterConnected();
        }

        @Override
        public void onLoginDataServiceFailed(Throwable errorContext) {
            // TODO Auto-generated method stub
            log.info("onLoginDataServiceFailed InsightErrorContext=" + errorContext);
        }

        @Override
        public void onLoginDataServiceResponse(long interactionId, LoginProto.LoginResponse loginResponse) {
            // TODO Auto-generated method stub
            log.info("onLoginDataServiceResponse interactionId=" + interactionId + ",LoginResponse="
                    + TextFormat.shortDebugString(loginResponse));
        }

        @Override
        public void onRingBufferFulled() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onRingBufferSize(long arg0, long arg1) {
            // TODO Auto-generated method stub

        }
}
