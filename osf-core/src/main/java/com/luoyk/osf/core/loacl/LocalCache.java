package com.luoyk.osf.core.loacl;

import com.luoyk.osf.core.cache.OsfCache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义的临时id,真实路径映射
 *
 * @author luoyk
 */
public class LocalCache implements OsfCache {

    private final int TIME_TO_LIVE;

    public static final Map<String, String> TEMP_ID_PATH_MAP = new HashMap<>();

    private final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            0L, TimeUnit.SECONDS,
            new SynchronousQueue<>());

    public LocalCache(int TIME_TO_LIVE) {
        this.TIME_TO_LIVE = TIME_TO_LIVE;
    }

    @Override
    public boolean newTempMap(String tempId, String path) {
        TEMP_ID_PATH_MAP.put(tempId, path);
        EXECUTOR_SERVICE.execute(() -> {
            try {
                Thread.sleep(TIME_TO_LIVE);
                TEMP_ID_PATH_MAP.remove(tempId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return true;
    }

    @Override
    public boolean removeTempId(String tempId) {
        TEMP_ID_PATH_MAP.remove(tempId);
        return true;
    }

    @Override
    public Optional<String> getPathByTempId(String tempId) {
        return Optional.ofNullable(TEMP_ID_PATH_MAP.get(tempId));
    }

}
