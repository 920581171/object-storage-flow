package com.luoyk.osf.core.loacl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.luoyk.osf.core.cache.OsfCache;

import java.time.Duration;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * 自定义的临时id,真实路径映射
 *
 * @author luoyk
 */
public class LocalCache implements OsfCache {

    private final Logger logger = Logger.getLogger(LocalCache.class.getName());

    private final Cache<String, String> TEMP_ID_PATH_MAP;

    public LocalCache(int timeToLive) {
        TEMP_ID_PATH_MAP = CacheBuilder.newBuilder()
                .removalListener(removalNotification ->
                        logger.info("remove cache key:" + removalNotification.getKey() +
                                ", value:" + removalNotification.getValue() +
                                ", cause by:" + removalNotification.getCause()))
                .expireAfterWrite(Duration.ofMillis(timeToLive))
                .build();
    }

    @Override
    public boolean newTempMap(String tempId, String path) {
        TEMP_ID_PATH_MAP.put(tempId, path);
        return true;
    }

    @Override
    public boolean removeTempId(String tempId) {
        TEMP_ID_PATH_MAP.invalidate(tempId);
        return true;
    }

    @Override
    public Optional<String> getPathByTempId(String tempId) {
        return Optional.ofNullable(TEMP_ID_PATH_MAP.getIfPresent(tempId));
    }

}
