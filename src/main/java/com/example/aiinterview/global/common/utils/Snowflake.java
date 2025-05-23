package com.example.aiinterview.global.common.utils;

public class Snowflake {
    private final static long EPOCH = 1288834974657L;
    private final static long DATA_CENTER_BITS = 5L;
    private final static long NODE_BITS = 5L;
    private final static long SEQ_BITS = 12L;

    private final static long MAX_DATA_CENTER_ID = (1L << DATA_CENTER_BITS) - 1;
    private final static long MAX_NODE_ID = (1L << NODE_BITS) - 1;
    private final static long MAX_SEQ = (1L << SEQ_BITS) - 1;

    private final long dataCenterId;
    private final long nodeId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public Snowflake(long dataCenterId, long nodeId) {
        if (dataCenterId < 0 || dataCenterId > MAX_DATA_CENTER_ID) {
            throw new IllegalArgumentException("Data Center ID can't be greater than " + MAX_DATA_CENTER_ID + " or less than 0");
        }
        if (nodeId < 0 || nodeId > MAX_NODE_ID) {
            throw new IllegalArgumentException("Node ID can't be greater than " + MAX_NODE_ID + " or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.nodeId = nodeId;
    }

    public synchronized long nextId() {
        long timestamp = currentTimeMillis();

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQ;
            if (sequence == 0) {
                while (timestamp <= lastTimestamp) {
                    timestamp = currentTimeMillis();
                }
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << (DATA_CENTER_BITS + NODE_BITS + SEQ_BITS)) |
                (dataCenterId << (NODE_BITS + SEQ_BITS)) |
                (nodeId << SEQ_BITS) |
                sequence;
    }

    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}