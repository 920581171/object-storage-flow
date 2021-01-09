package com.luoyk.osf.core.definition.achieve;

import com.luoyk.osf.core.definition.OsfAction;
import com.luoyk.osf.core.exception.OsfException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片类型的相关操作
 *
 * @author luoyk
 */
public abstract class PictureAction implements OsfAction {

    /**
     * 重写该方法，以提供保存临时文件的方法
     *
     * @return tempId
     */
    protected abstract String saveTempProvider(String filename, InputStream inputStream);

    /**
     * 重写该方法，以提供保存正式文件的方法
     *
     * @return 主图片路径
     */
    protected abstract String transferFileProvider(String tempId);

    /**
     * 重写该方法，删除主文件
     *
     * @param file 主文件地址
     * @return 删除是否成功
     */
    protected abstract boolean deleteProvider(String file);

    /**
     * 重写该方法，删除主临时文件
     *
     * @param file 主文件地址
     * @return 删除是否成功
     */
    protected abstract boolean deleteTempProvider(String file);

    /**
     * 重写该方法，生成缩略图
     *
     * @throws OsfException 抛出异常
     */
    protected abstract void makeThumbnail(String tempId, String filename, InputStream inputStream) throws OsfException;

    /**
     * 重写该方法，保存缩略图
     *
     * @throws OsfException 抛出异常
     */
    protected abstract void transferThumbnail(String tempId) throws OsfException;

    /**
     * 重写该方法，删除缩略图
     *
     * @throws OsfException 抛出异常
     */
    protected abstract void deleteThumbnail(String file) throws OsfException;

    /**
     * 重写该方法，删除缓存缩略图
     *
     * @throws OsfException 抛出异常
     */
    protected abstract void deleteTempThumbnail(String file) throws OsfException;

    /**
     * 保存临时文件的同时保存缩略图
     *
     * @param filename    文件名称
     * @param inputStream 文件输入流
     * @return 主文件临时地址
     */
    @Override
    public final String saveTemp(String filename, InputStream inputStream) {

        InputStream resetStream = inputStream.markSupported() ?
                inputStream :
                copyMarkSteam(inputStream);

        try {
            String tempId = saveTempProvider(filename, resetStream);
            resetStream.reset();
            makeThumbnail(tempId, filename, resetStream);
            return tempId;
        } catch (IOException e) {
            throw new OsfException("Reset stream error", e);
        }
    }

    /**
     * 转存临时文件到正式文件，
     *
     * @param tempId 临时文件
     * @return 正式文件访问路径
     */
    @Override
    public final String transferFile(String tempId) {
        transferThumbnail(tempId);
        return transferFileProvider(tempId);
    }

    /**
     * 删除文件的同时删除缩略图
     *
     * @param file 文件输入流
     * @return 主文件是否删除成功
     */
    @Override
    public boolean delete(String file) {
        boolean deleted = deleteProvider(file);
        if (deleted) {
            deleteThumbnail(file);
        }
        return deleted;
    }

    /**
     * 删除临时文件的删除缩略图
     *
     * @param file 文件输入流
     * @return 主文件是否删除成功
     */
    @Override
    public boolean deleteTemp(String file) {
        boolean deleted = deleteTempProvider(file);
        if (deleted) {
            deleteTempThumbnail(file);
        }
        return deleted;
    }

    public enum PictureSizeEnum {

        /**
         * 大图
         */
        LARGE("large", 512),

        /**
         * 中图
         */
        MEDIUM("medium", 256),

        /**
         * 小图
         */
        SMALL("small", 128);

        private final String fixName;

        private final int size;

        public final String fixName() {
            return fixName;
        }

        public final int size() {
            return size;
        }

        PictureSizeEnum(String fixName, int size) {
            this.fixName = fixName;
            this.size = size;
        }
    }

    /**
     * 把不支持reset的inputStream转换为支持reset的inputStream
     */
    public InputStream copyMarkSteam(InputStream inputStream) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byteArrayOutputStream.flush();

            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        } catch (Exception e) {
            throw new OsfException("Copy stream error", e);
        }
    }
}
