package com.luoyk.osf.core.mq;

/**
 * 延迟消息
 */
public class DelayMessage {

    /**
     * 临时文件Id
     */
    private String tempId;

    /**
     * 临时文件保存时间
     */
    private long savaTempTime;

    /**
     * 文件类型
     */
    private FileTypeEnum fileType;

    public String getTempId() {
        return tempId;
    }

    public DelayMessage setTempId(String tempId) {
        this.tempId = tempId;
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
}
