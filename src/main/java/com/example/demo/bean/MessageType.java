package com.example.demo.bean;

public enum MessageType {
    UNKNOWN_MESSAGE_TYPE(0, 0),
    HEARTBEAT_REQUEST(1, 1),
    HEARTBEAT_RESPONSE(2, 2),
    GENERAL_ERROR_MESSAGE(3, 10),
    LOGIN_REQUEST(4, 11),
    LOGIN_RESPONSE(5, 12),
    SERVICE_DISCOVERY_REQUEST(6, 13),
    SERVICE_DISCOVERY_RESPONSE(7, 14),
    MD_SUBSCRIBE_REQUEST(8, 15),
    MD_SUBSCRIBE_RESPONSE(9, 16),
    PUSH_MARKET_DATA(10, 17),
    MD_QUERY_REQUEST(11, 18),
    MD_QUERY_RESPONSE(12, 19),
    PLAYBACK_REQUEST(13, 20),
    PLAYBACK_RESPONSE(14, 21),
    PLAYBACK_CONTROL_REQUEST(15, 22),
    PLAYBACK_CONTROL_RESPONSE(16, 23),
    PLAYBACK_STATUS_REQUEST(17, 24),
    PLAYBACK_STATUS(18, 25),
    PLAYBACK_PAYLOAD(19, 26),
    PUSH_MARKET_DATA_STREAM(20, 27),

    FILL_RESPONSE(30, 30),
    ;


    private final int index;
    private final int value;

    private MessageType(int index, int value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public int getValue() {
        return value;
    }
}
