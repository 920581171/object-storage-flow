package com.luoyk.osf.core.loacl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.luoyk.osf.core.cache.OsfCache;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 自定义的临时id,真实路径映射
 *
 * @author luoyk
 */
public class LocalCache implements OsfCache {

    private final Logger logger = Logger.getLogger(LocalCache.class.getName());

    private final Cache<String, String> tempIdPathMap;

    private final ConcurrentHashMap<String, String> hashMap = new ConcurrentHashMap<>();

    public LocalCache(int timeToLive) {
        tempIdPathMap = CacheBuilder.newBuilder()
                .removalListener(removalNotification ->
                        logger.info("remove cache key:" + removalNotification.getKey() +
                                ", value:" + removalNotification.getValue() +
                                ", cause by:" + removalNotification.getCause()))
                .expireAfterWrite(Duration.ofMillis(timeToLive))
                .build();
    }

    @Override
    public boolean newTempMap(String tempId, String path) {
        hashMap.put(tempId, path);
        return true;
    }

    @Override
    public boolean removeTempId(String tempId) {
        hashMap.remove(tempId);
        return true;
    }

    @Override
    public Optional<String> getPathByTempId(String tempId) {
        return Optional.ofNullable(hashMap.get(tempId));
    }

    @Override
    public boolean newCountDownTempMap(String tempId, String path) {
        tempIdPathMap.put(tempId, path);
        return true;
    }

    @Override
    public boolean removeCountDownTempId(String tempId) {
        tempIdPathMap.invalidate(tempId);
        return true;
    }

    @Override
    public Optional<String> getCountDownPathByTempId(String tempId) {
        return Optional.ofNullable(tempIdPathMap.getIfPresent(tempId));
    }

}
