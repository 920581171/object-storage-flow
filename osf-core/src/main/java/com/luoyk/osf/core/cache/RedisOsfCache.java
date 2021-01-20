package com.luoyk.osf.core.cache;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

/**
 * 基于Redis的缓存控制
 *
 * @author luoyk
 */
public class RedisOsfCache implements OsfCache {

    private final int timeToLive;

    private final RedisTemplate<Object,Object> redisTemplate;

    public RedisOsfCache(int timeToLive, RedisTemplate<Object,Object> redisTemplate) {
        this.timeToLive = timeToLive;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean newTempMap(String tempId, String path) {
        redisTemplate.opsForValue().set(tempId,path);
        return true;
    }

    @Override
    public boolean removeTempId(String tempId) {
        redisTemplate.opsForValue().get(tempId);
        return true;
    }

    @Override
    public Optional<String> getPathByTempId(String tempId) {
        return Optional.ofNullable((String) redisTemplate.opsForValue().get(tempId));
    }

    @Override
    public boolean newCountDownTempMap(String tempId, String path) {
        redisTemplate.opsForValue().set(tempId,path,timeToLive);
        return true;
    }

    @Override
    public boolean removeCountDownTempId(String tempId) {
       return Optional.ofNullable(redisTemplate.delete(tempId)).orElse(false);
    }

    @Override
    public Optional<String> getCountDownPathByTempId(String tempId) {
        return Optional.ofNullable((String) redisTemplate.opsForValue().get(tempId));
    }
}
