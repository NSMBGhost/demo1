package com.example.demo.bean;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "project")
public class ParamHandler {

    @Getter
    private Delayq delayq;

    @Getter
    private Debug debug;

    @Getter
    private Notify notify;

    @Getter
    private Startup startup;

    @Getter
    private Tick tick;

    private Future future;

    @Data
    public static class Delayq {
        private String server;
        private Integer origin;
    }

    @Data
    public static class Debug {
        private boolean market;
        private boolean sqlShow;
    }

    @Data
    public static class Notify {
        private String secretKey;
    }

    @Data
    public static class Tick {
        private String appendUrl;
        private String originFolder;
    }

    @Data
    public static class Future {
        private MainContract mainContract;
    }

    @Data
    public static class MainContract {
        private String url;
        private String method;
    }

    @Data
    public static class Startup {
        private boolean connectInsight;
        private boolean autoPadding;
        private boolean enableTaskExecutor;
    }

}
