package com.luoyk.osf.core.mq;

/**
 * 定义消息队列接收操作
 *
 * @author luoyk
 */
public interface OsfReceiver {

    /**
     * 接收延迟消息
     *
     * @param delayMessage 消息内容
     * @return 处理成功或失败
     */
    boolean receive(DelayMessage delayMessage);
}
