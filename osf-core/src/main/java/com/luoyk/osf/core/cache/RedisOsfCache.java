package com.luoyk.osf.core.cache;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

/**
 * 基于Redis的缓存控制
 *
 * @author luoyk
 */
public class RedisOsfCache implements OsfCache {

    private final int TIME_TO_LIVE;

    private final RedisTemplate<?, ?> redisTemplate;

    public RedisOsfCache(int TIME_TO_LIVE, RedisTemplate<?, ?> redisTemplate) {
        this.TIME_TO_LIVE = TIME_TO_LIVE;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean newTempMap(String tempId, String path) {
        return false;
    }

    @Override
    public boolean removeTempId(String tempId) {
        return false;
    }

    @Override
    public Optional<String> getPathByTempId(String tempId) {
        return Optional.empty();
    }
}
