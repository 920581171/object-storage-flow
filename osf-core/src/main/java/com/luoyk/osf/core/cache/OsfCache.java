package com.luoyk.osf.core.cache;

import java.util.Optional;

/**
 * 定义缓存操作类
 *
 * @author luoyk
 */
public interface OsfCache {

    /**
     * 无倒计时的缓存tempId,path
     *
     * @param tempId tempId;
     * @param path   path;
     * @return 是否缓存成功
     */
    boolean newTempMap(String tempId, String path);

    /**
     * 移除无倒计时的tempId
     *
     * @param tempId tempId
     * @return 是否移除成功
     */
    boolean removeTempId(String tempId);

    /**
     * 获得无倒计时tempId所对应的path
     *
     * @param tempId tempId
     * @return Optional
     */
    Optional<String> getPathByTempId(String tempId);

    /**
     * 带有倒计时的缓存tempId,path
     *
     * @param tempId tempId;
     * @param path   path;
     * @return 是否缓存成功
     */
    boolean newCountDownTempMap(String tempId, String path);

    /**
     * 移除带有倒计时的tempId
     *
     * @param tempId tempId
     * @return 是否移除成功
     */
    boolean removeCountDownTempId(String tempId);

    /**
     * 获得倒计时的tempId所对应的path
     *
     * @param tempId tempId
     * @return Optional
     */
    Optional<String> getCountDownPathByTempId(String tempId);
}
