package com.luoyk.osf.core.definition.achieve;

import com.luoyk.osf.core.definition.Action;
import com.luoyk.osf.core.definition.Osf;
import com.luoyk.osf.core.definition.achieve.FileAction;
import com.luoyk.osf.core.definition.achieve.PictureAction;
import com.luoyk.osf.core.exception.OsfException;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.util.MimeTypeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * @author luoyk
 */
public abstract class AbstractOsf implements Osf {

    public final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * MimeType BMP文件
     */
    public static final String MIME_TYPE_BMP = "image/bmp";

    /**
     * 用于保存默认和自定义的操作流程
     */
    private final HashMap<Class<? extends Action>, Action> actionMap = new HashMap<>();

    public AbstractOsf() {
        actionMap.put(FileAction.class, fileActionProvider());
        actionMap.put(PictureAction.class, pictureActionProvider());
        for (Action action : customActionProvider()) {
            if (action instanceof FileAction || action instanceof PictureAction) {
                throw new RuntimeException("FileAction or PictureAction existed");
            }
            actionMap.put(action.getClass(), action);
        }
    }

    /**
     * 生成默认的TempId
     *
     * @return TempId
     */
    public String getTempUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获得文件后缀名
     *
     * @param filename 文件名
     * @return suffix name
     */
    public String getFileSuffixName(String filename) {
        final int lastDot = filename.lastIndexOf('.');
        return filename.substring(lastDot + 1);
    }

    /**
     * 获得新的文件名
     *
     * @param prefix 文件名
     * @param suffix 后缀名
     * @return ${prefix}.${suffix}
     */
    public String concatFilename(String prefix, String suffix) {
        return prefix + '.' + suffix;
    }

    /**
     * 查找文件的 mimeType
     *
     * @param filename 文件名
     * @return mimetype/content-type
     */
    public String getMimeType(String filename) {
        final String contentType = URLConnection.getFileNameMap().getContentTypeFor(filename);
        if (contentType == null) {
            return MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;
        }
        return contentType;
    }

    /**
     * 判断文件是否是图片
     *
     * @param filename 文件名
     * @return 是否是图片类型
     */
    public boolean isImage(String filename) {
        switch (getMimeType(filename)) {
            case MimeTypeUtils.IMAGE_PNG_VALUE:
            case MimeTypeUtils.IMAGE_GIF_VALUE:
            case MimeTypeUtils.IMAGE_JPEG_VALUE:
            case MIME_TYPE_BMP:
                return true;
            default:
                return false;
        }
    }

    /**
     * 压缩/转换图片
     *
     * @param inputStream 图片文件输入流
     * @param suffix      后缀名
     * @return 图片文件字节
     */
    public byte[] compressImage(InputStream inputStream, String suffix) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Thumbnails.of(inputStream)
                    .scale(1)
                    .outputFormat(suffix)
                    .toOutputStream(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new OsfException("Compress image error: " + e.getMessage());
        }
    }

    /**
     * 压缩/转换图片
     *
     * @param inputStream 图片文件输入流
     * @param suffix      后缀名
     * @param width       图片宽度
     * @param height      图片高度
     * @return 图片文件字节
     */
    public byte[] resizeImage(InputStream inputStream, String suffix, int width, int height) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Thumbnails.of(inputStream)
                    .size(width, height)
                    .outputFormat(suffix)
                    .toOutputStream(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new OsfException("Resize image error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Action> T getAction(Class<T> actionAchieveClass) {
        return (T) actionMap.get(actionAchieveClass);
    }

    public FileAction getFileAction() {
        return (FileAction) actionMap.get(FileAction.class);
    }

    public PictureAction getPictureAction() {
        return (PictureAction) actionMap.get(PictureAction.class);
    }


    protected abstract FileAction fileActionProvider();

    protected abstract PictureAction pictureActionProvider();

    protected List<Action> customActionProvider() {
        return Collections.emptyList();
    }
}
