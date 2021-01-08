package com.luoyk.osf.core.mq;

import java.time.Instant;

/**
 * 延迟消息
 */
public class DelayMessage {

    /**
     * 临时文件地址
     */
    private String tempPath;

    /**
     * 临时文件保存时间
     */
    private long savaTempTime;

    public static DelayMessage newFileDelayMessage(String tempPath) {
        return new DelayMessage()
                .setFileType(FileTypeEnum.FILE)
                .setSavaTempTime(Instant.now().toEpochMilli())
                .setTempPath(tempPath);
    }

    public static DelayMessage newPictureDelayMessage(String tempId) {
        return new DelayMessage()
                .setFileType(FileTypeEnum.PICTURE)
                .setSavaTempTime(Instant.now().toEpochMilli())
                .setTempPath(tempId);
    }

    /**
     * 文件类型
     */
    private FileTypeEnum fileType;

    public String getTempPath() {
        return tempPath;
    }

    public DelayMessage setTempPath(String tempPath) {
        this.tempPath = tempPath;
        return this;
    }

    public long getSavaTempTime() {
        return savaTempTime;
    }

    public DelayMessage setSavaTempTime(long savaTempTime) {
        this.savaTempTime = savaTempTime;
        return this;
    }

    public FileTypeEnum getFileType() {
        return fileType;
    }

    public DelayMessage setFileType(FileTypeEnum fileType) {
        this.fileType = fileType;
        return this;
    }

    public enum FileTypeEnum {
        FILE, PICTURE
    }

    @Override
    public String toString() {
        return "DelayMessage{" +
                "tempPath='" + tempPath + '\'' +
                ", savaTempTime=" + savaTempTime +
                ", fileType=" + fileType +
                '}';
    }
}
