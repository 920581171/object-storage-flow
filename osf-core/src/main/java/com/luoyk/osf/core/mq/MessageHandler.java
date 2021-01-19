package com.luoyk.osf.core.mq;

/**
 * 定义自定义消息处理接口
 *
 * @author luoyk
 */
public interface MessageHandler {

    /**
     * 消息处理
     *
     * @param delayMessage 消息内容
     * @return 处理结果
     */
    boolean handler(DelayMessage delayMessage);
}
