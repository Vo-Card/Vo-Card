package com.voc.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.voc.database.DatabaseUtils;
import com.voc.database.SQLResult;
import com.voc.utils.Row;

public class WorkerSessionManager {

    public static final String CACHE_FILE = "cache/.worker-cache";

    private static final int HEARTBEAT_INTERVAL_SEC = 30;

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static String uuid;
    private static long workerId;

    public static void InitializeWorker(String hostname) throws SQLException, IOException {
        Path cacheFile = Paths.get(CACHE_FILE);
        if (Files.exists(cacheFile)) {
            uuid = Files.readString(cacheFile).trim();
        } else {
            uuid = UUID.randomUUID().toString();
            Files.createDirectories(cacheFile.getParent());
            Files.writeString(cacheFile, uuid);
        }

        workerId = registerWorker(uuid, hostname);

        scheduler.scheduleAtFixedRate(() -> {
            sendHeartbeat(workerId);
        }, 0, HEARTBEAT_INTERVAL_SEC, TimeUnit.SECONDS);

        System.out.println("Worker initialized: UUID=" + uuid + ", workerId=" + workerId);
    }

    public static long registerWorker(String uuid, String hostname) throws SQLException {
        String sql = "INSERT INTO workertb (uuid, hostname, last_heartbeat, expires_at) " +
                       "VALUES (?, ?, NOW(), NOW() + INTERVAL 7 DAY) " +
                       "ON DUPLICATE KEY UPDATE last_heartbeat = NOW(), expires_at = NOW() + INTERVAL 7 DAY";

        SQLResult result = DatabaseUtils.sqlPrepareStatement(sql, uuid, hostname);
        long tempWorkerId = result.getGeneratedKey();
        if (tempWorkerId != -1) {
            return tempWorkerId;
        }

        sql = "SELECT worker_id FROM workertb WHERE uuid = ?";
        Row workerQueryid = DatabaseUtils.sqlSingleRowStatement(sql, uuid);
        if (workerQueryid != null) {
            return ((Number) workerQueryid.get("worker_id")).longValue();
        } else {
            throw new SQLException("Failed to fetch worker ID");
        }
    }

    private static void sendHeartbeat(long workerId) {
        String sql = "UPDATE workertb SET last_heartbeat = NOW(), expires_at = NOW() + INTERVAL 7 DAY WHERE worker_id = ?";
        SQLResult result = DatabaseUtils.sqlPrepareStatement(sql, workerId);
        if (!result.isSuccess()){
            System.err.println("Worker heartbeat, heart choke");
        }
    }

    public static void shutdownScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            System.out.println("Scheduler stopped.");
        }
    }

    public static long getWorkerId() {
        return workerId;
    }
}
