package com.luoyk.osf.core.definition;

import java.io.InputStream;

/**
 * 定义了OSF中最基本的操作
 */
public interface OsfAction extends Action {
    /**
     * 保存临时文件，获取临时Id
     *
     * @param filename    文件名称
     * @param inputStream 文件输入流
     * @return 临时文件Id
     */
    String saveTemp(String filename, InputStream inputStream);

    /**
     * 转存临时文件到正式文件，
     *
     * @param tempId 临时文件
     * @return 正式文件访问路径
     */
    String transferFile(String tempId);

    /**
     * 保存并替换旧文件
     *
     * @param tempId  临时文件Id
     * @param oldFile 旧文件，根据文件系统的不同可自定义为Id或者地址
     * @return 正式文件地址
     */
    String transferReplace(String tempId, String oldFile);

    /**
     * 删除文件
     *
     * @param file 文件id或者地址
     * @return 是否删除成功
     */
    boolean delete(String file);

    /**
     * 删除缓存文件
     *
     * @param tempPath 临时文件路径
     * @return 是否删除成功
     */
    boolean deleteTemp(String tempPath);
}
