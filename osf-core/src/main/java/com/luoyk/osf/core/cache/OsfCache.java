package com.luoyk.osf.core.cache;

import java.util.Optional;

/**
 * 定义缓存操作类
 *
 * @author luoyk
 */
public interface OsfCache {

    /**
     * 缓存tempId,path
     *
     * @param tempId tempId;
     * @param path   path;
     * @return 是否缓存成功
     */
    boolean newTempMap(String tempId, String path);

    /**
     * 移除tempId
     *
     * @param tempId tempId
     * @return 是否移除成功
     */
    boolean removeTempId(String tempId);

    /**
     * 获得tempId所对应的path
     *
     * @param tempId tempId
     * @return Optional
     */
    Optional<String> getPathByTempId(String tempId);
}
