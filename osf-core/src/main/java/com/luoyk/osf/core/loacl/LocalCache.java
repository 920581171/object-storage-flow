package com.luoyk.osf.core.loacl;

import com.luoyk.osf.core.cache.OsfCache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 自定义的临时id,真实路径映射
 *
 * @author luoyk
 */
public class LocalCache implements OsfCache {

    private final int timeToLive;

    public static final Map<String, String> TEMP_ID_PATH_MAP = new HashMap<>();

    public LocalCache(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public boolean newTempMap(String tempId, String path) {
        TEMP_ID_PATH_MAP.put(tempId, path);
        LocalThreadPool.EXECUTOR_SERVICE.execute(() -> {
            try {
                Thread.sleep(timeToLive);
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
