package com.voc.server;

public class Snowflake {
    
    // Epoch 2020-01-01
    private static final long epoch = 1577836800000L;

    // Bit allocations
    private static final long workerIdBits = 10L;
    private static final long sequenceBits = 12L;

    // Max values
    private static final long maxWorkerId = ~(-1L << workerIdBits);
    private static final long sequenceMask = ~(-1L << sequenceBits);

    // Bit shifts
    private static final long workerIdShift = sequenceBits;
    private static final long timestampShift = sequenceBits + workerIdBits;

    private static long workerId;

    private static long lastTimestamp = -1L;
    private static long sequence = 0L;

    /**
     * This will be initialize during server start along side with
     * WorkerSessionManager.java
     * 
     * @param workerId WorkerId from the server manager
     */
    public static void InitializeSnowflake(long addWorkerId) {
        if (addWorkerId > maxWorkerId || addWorkerId < 0) {
            throw new IllegalArgumentException("Worker ID out of range");
        }
        workerId = addWorkerId;
    }

    public static synchronized long nextId() {
        long timestamp = currentTime();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id.");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << timestampShift) |
               (workerId << workerIdShift) |
               sequence;
    }

    private static long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTime();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTime();
        }
        return timestamp;
    }

    private static long currentTime() {
        return System.currentTimeMillis();
    }
}
